/*
 * Copyright 2026 Peanut Butter Unicorn, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lol.pbu.z4j.client

import io.micronaut.http.client.exceptions.HttpClientResponseException
import lol.pbu.z4j.Z4jSpec
import lol.pbu.z4j.model.*
import spock.lang.Shared
import spock.lang.Unroll

class SearchClientSpec extends Z4jSpec {
    @Shared
    SearchClient adminSearchClient, agentSearchClient, userSearchClient

    def setupSpec() {
        adminSearchClient = adminCtx.getBean(SearchClient.class)
        agentSearchClient = agentCtx.getBean(SearchClient.class)
        userSearchClient = userCtx.getBean(SearchClient.class)
    }

    /* ---------- list() tests --------------- */

    @SuppressWarnings("GroovyAssignabilityCheck")
    @Unroll("an #clientName user can run the list method with sortby: #sortBy, sortOrder: #sortOrder")
    void "can run the list method"(String clientName, SearchClient client, SortBy sortBy, SortOrder sortOrder) {
        when:
        client.list("type:group", sortBy, sortOrder, null, null).block()

        then:
        noExceptionThrown()

        where:
        [[client, clientName], sortBy, sortOrder] << [[[adminSearchClient, "admin"], [agentSearchClient, "agent"]],
                                                      [SortBy.values(), null].flatten(),
                                                      [SortOrder.values(), null].flatten()].combinations()
    }

    void "can query the list method when results include more than the page size"() {
        given:
        String ticketQuery = "type:ticket"
        def ticketCount = adminSearchClient.count(ticketQuery).block().getCount()
        if (ticketCount < 5) {
            (ticketCount..5).each { createTicketForTest() }
        }

        when:
        def page = 1
        List<SearchResponse> responses = []
        SearchResponse response = adminSearchClient.list(ticketQuery, null, null, page, 2).block()
        responses << response
        while (response.nextPage != null && page < 3) {
            response = adminSearchClient.list(ticketQuery, null, null, page, 2).block()
            page++
            responses << response
        }

        then:
        noExceptionThrown()
    }

    void "can run search with sideloading for users, organizations, and groups"() {
        when: "searching tickets with sideloading"
        SearchResponse response = adminSearchClient.list("type:ticket", "tickets(users,organizations,groups)", null, null, 1, 5).block()

        then:
        noExceptionThrown()
        response != null
        response.results != null
        response.users != null
        !response.users.isEmpty()
    }

    @Unroll("a simple user querying the list method fails with #sortBy and #sortOrder")
    void "cannot run searchClient.list()"(SearchClient client, SortBy sortBy, SortOrder sortOrder) {
        when:
        client.list(faker.bluey().quote(), sortBy, sortOrder, null, null).block()

        then:
        thrown(HttpClientResponseException)

        where:
        [client, sortBy, sortOrder] << [[userSearchClient],
                                        [SortBy.values(), null].flatten(),
                                        [SortOrder.values(), null].flatten(),
                                        [null, faker.cat().name()]].combinations()
    }

    /* ---------- count() tests --------------- */

    void "an #clientName user can run the count method"(String clientName, SearchClient client) {
        when:
        client.count(faker.bluey().quote()).block()

        then:
        noExceptionThrown()

        where:
        [client, clientName] << [[adminSearchClient, "admin"], [agentSearchClient, "agent"]]
    }


    void "a normal user cannot run the count method"(SearchClient client) {
        when:
        client.count(faker.bluey().quote()).block()

        then:
        thrown(HttpClientResponseException)

        where:
        client           | _
        userSearchClient | _
    }

    /* ---------- exportTicket() tests --------------- */

    @SuppressWarnings("GroovyAssignabilityCheck")
    void "an #clientName can call export method and paginate properly"(String clientName, SearchClient client) {
        given:
        String query = "type:ticket"
        def count = client.count(query).block()
        int pageSize = Math.max(2, Math.min(1000, (count.getCount() / 2).intValue() + 1))

        when:
        ExportResponse<Ticket> response = client.exportTicket(query, pageSize, null).block()
        List<Ticket> tickets = response.getResults()

        and:
        while (response.getMeta().getAfterCursor() != null && response.getMeta().getHasMore()) {
            response = client.exportTicket(query, pageSize, response.getMeta().getAfterCursor()).block()
            tickets.addAll(response.getResults())
        }

        then:
        noExceptionThrown()
        tickets.size() > (count.getCount() * 0.95)
        tickets.size() < (count.getCount() * 1.05)


        where:
        [[client, clientName]] << [[[adminSearchClient, "admin"], [agentSearchClient, "agent"]]].combinations()
    }
}
