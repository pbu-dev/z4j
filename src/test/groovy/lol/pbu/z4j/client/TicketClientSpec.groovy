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

import io.micronaut.http.client.exceptions.HttpClientException
import lol.pbu.z4j.Z4jSpec
import lol.pbu.z4j.fixture.FixtureLoader
import lol.pbu.z4j.fixture.TicketFixtures
import lol.pbu.z4j.fixture.TicketItem
import lol.pbu.z4j.model.*
import spock.lang.Shared

class TicketClientSpec extends Z4jSpec {

    @Shared
    TicketClient ticketsAgentClient, ticketsUserClient, ticketBadEmailClient, ticketBadUrlClient

    @Shared
    List<Ticket> tickets

    @Shared
    List<Map> clientTestMatrix

    @Shared
    TicketFixtures ticketFixtures

    void setupSpec() {
        ticketBadEmailClient = badEmailCtx.getBean(TicketClient.class)
        ticketBadUrlClient = badUrlCtx.getBean(TicketClient.class)
        ticketsAgentClient = agentCtx.getBean(TicketClient.class)
        ticketsAdminClient = ticketsAdminClient ?: adminCtx.getBean(TicketClient.class)
        ticketsUserClient = userCtx.getBean(TicketClient.class)
        tickets = ticketsAgentClient.listTickets(null).block().getTickets()
        ticketFixtures = FixtureLoader.loadFixture("/fixtures/ticket_fixtures.yaml", TicketFixtures.class)
        clientTestMatrix = [[client: ticketsAgentClient, clientType: "Agent", shouldSucceed: true, expectedTitle: "should"],
                            [client: ticketsAdminClient, clientType: "Admin", shouldSucceed: true, expectedTitle: "should"],
                            [client: ticketBadEmailClient, clientType: "bad email", shouldSucceed: false, expectedTitle: "should not"],
                            [client: ticketBadUrlClient, clientType: "bad url", shouldSucceed: false, expectedTitle: "should not"],
                            [client: ticketsUserClient, clientType: "simple user", shouldSucceed: false, expectedTitle: "should not"]]
    }

    def "calling listTickets() succeeds when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        when:
        client.listTickets(null).block()

        then:
        noExceptionThrown()

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { it.shouldSucceed }
    }

    def "calling listTickets() fails when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        when:
        client.listTickets(null).block()

        then:
        thrown(HttpClientException)

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { !it.shouldSucceed }
    }

    def "calling showTicket() succeeds when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        when:
        client.showTicket(tickets.get(0).getId()).block()

        then:
        noExceptionThrown()

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { it.shouldSucceed }
    }

    def "calling showTicket() fails when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        when:
        client.showTicket(tickets.get(0).getId()).block()

        then:
        thrown(HttpClientException)

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { !it.shouldSucceed }
    }

    def "calling listAuditsForTicket() succeeds when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        when:
        TicketAuditsResponse response = client.listAuditsForTicket(tickets.get(0).getId()).block()

        then:
        noExceptionThrown()
        response != null
        response.audits != null
        !response.audits.isEmpty()
        response.audits.first().ticketId == tickets.get(0).getId()
        response.audits.first().events != null

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { it.shouldSucceed }
    }

    def "calling listAuditsForTicket() fails when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        when:
        client.listAuditsForTicket(tickets.get(0).getId()).block()

        then:
        thrown(HttpClientException)

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { !it.shouldSucceed }
    }

    def "Trying to create a ticket succeeds when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        given:
        TicketItem sampleData = ticketFixtures.getTicketData().first()
        TicketComment ticketComment = new TicketComment().setBody(sampleData.getComment())
        TicketCreateInput createTicketInput = new TicketCreateInput(ticketComment)
        createTicketInput.setRawSubject(sampleData.getSubject())
        TicketCreateRequest createTicketRequest = new TicketCreateRequest(createTicketInput)

        when:
        client.createTicket(createTicketRequest).block()

        then:
        noExceptionThrown()

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { it.shouldSucceed }
    }

    def "Trying to create a ticket fails when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        given:
        TicketItem sampleData = ticketFixtures.getTicketData().first()
        TicketComment ticketComment = new TicketComment().setBody(sampleData.getComment())
        TicketCreateInput createTicketInput = new TicketCreateInput(ticketComment)
        createTicketInput.setRawSubject(sampleData.getSubject())
        TicketCreateRequest createTicketRequest = new TicketCreateRequest(createTicketInput)

        when:
        client.createTicket(createTicketRequest).block()

        then:
        thrown(HttpClientException)

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { !it.shouldSucceed }
    }

    def "calling updateTicket() succeeds when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        given:
        TicketItem sampleData = ticketFixtures.getTicketData().first()
        TicketUpdateInput ticketUpdateInput = new TicketUpdateInput()
                .setComment(new TicketComment().setBody(sampleData.getUpdateComment()))
        TicketUpdateRequest ticketUpdateRequest = new TicketUpdateRequest().setTicket(ticketUpdateInput)


        when:
        client.updateTicket(tickets.first.getId(), ticketUpdateRequest).block()

        then:
        noExceptionThrown()

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { it.shouldSucceed }
    }

    def "calling updateTicket() fails when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        given:
        TicketItem sampleData = ticketFixtures.getTicketData().first()
        TicketUpdateInput ticketUpdateInput = new TicketUpdateInput()
                .setComment(new TicketComment().setBody(sampleData.getUpdateComment()))
        TicketUpdateRequest ticketUpdateRequest = new TicketUpdateRequest().setTicket(ticketUpdateInput)


        when:
        client.updateTicket(tickets.first.getId(), ticketUpdateRequest).block()


        then:
        thrown(HttpClientException)

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { !it.shouldSucceed }
    }


    def "calling countTickets() succeeds when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        when:
        client.getTicketCount().block()

        then:
        noExceptionThrown()

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { it.shouldSucceed }
    }

    def "calling countTickets() fails when used with a(n) #clientType client"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored) {
        when:
        client.getTicketCount().block()

        then:
        thrown(HttpClientException)

        where:
        [client, clientType, ignored, alsoIgnored] << clientTestMatrix.findAll { !it.shouldSucceed }
    }

    def "can call listTicketFields when using a(n) #clientType client and #creator flag"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored, Boolean creator, Locale locale) {
        when:
        client.listTicketFields(locale.getLocaleAbbreviation(), creator).block()

        then:
        noExceptionThrown()

        where:
        [[client, clientType, ignored, alsoIgnored], creator, locale] << [
                clientTestMatrix.findAll { it.shouldSucceed || it.clientType == "simple user" }, [true, false, null], accountLocales
        ].combinations()
    }

    def "calling listTicketFields when using a(n) #clientType client and #creator flag fails"(TicketClient client, String clientType, Boolean ignored, String alsoIgnored, Boolean creator, Locale locale) {
        when:
        client.listTicketFields(locale.getLocaleAbbreviation(), creator).block()

        then:
        thrown(HttpClientException)

        where:
        [[client, clientType, ignored, alsoIgnored], creator, locale] << [
                clientTestMatrix.findAll { !it.shouldSucceed && it.clientType != "simple user"}, [true, false, null], accountLocales
        ].combinations()
    }

    def "can paginate listTicketFields with cursor pagination"() {
        given:
        LocaleAbbreviation locale = accountLocales.first().getLocaleAbbreviation()
        ensureMoreThan100TicketFields(locale)

        when:
        TicketFieldsResponse page = ticketsAdminClient.listTicketFields(locale, null, null, 100).block()
        List<TicketField> allFields = []
        while (page != null) {
            if (page.getTicketFields() != null) {
                allFields.addAll(page.getTicketFields())
            }
            if (!(page.getMeta()?.getHasMore()) || page.getMeta()?.getAfterCursor() == null) {
                break
            }
            page = ticketsAdminClient.listTicketFields(locale, null, page.getMeta().getAfterCursor(), 100).block()
        }

        then:
        allFields.size() > 100
        allFields*.id.toSet().size() == allFields.size()
    }

    private void ensureMoreThan100TicketFields(LocaleAbbreviation locale) {
        TicketFieldsResponse firstPage = ticketsAdminClient.listTicketFields(locale, null, null, 100).block()
        if (firstPage.getMeta()?.getHasMore()) {
            return
        }

        int needed = Math.max(0, 101 - (firstPage.getTicketFields()?.size() ?: 0))
        (1..needed).each {
            String entropy = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
            TicketField field = new TicketField()
                    .setTitle("z4j-cursor-fixture-${entropy}")
                    .setType(TicketFieldTypeEnum.TEXT.getValue())
            ticketsAdminClient.createTicketField(new TicketFieldCreateRequest(field)).block()
        }

        int maxAttempts = 10
        for (int i = 0; i < maxAttempts; i++) {
            TicketFieldsResponse refreshed = ticketsAdminClient.listTicketFields(locale, null, null, 100).block()
            if (refreshed.getMeta()?.getHasMore()) {
                return
            }
            sleep(2000)
        }
        throw new IllegalStateException("Ticket field fixture setup did not become visible in time. Please rerun or pre-seed your sandbox.")
    }

    def "can show and delete a ticket field as admin"() {
        given: "a created custom ticket field"
        String entropy = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        TicketField field = new TicketField("z4j-field-${entropy}", TicketFieldTypeEnum.TEXT.getValue())
        TicketFieldResponse created = ticketsAdminClient.createTicketField(new TicketFieldCreateRequest(field)).block()
        Long fieldId = created.getTicketField().getId()

        when: "fetching the ticket field by id"
        TicketFieldResponse shown = ticketsAdminClient.showTicketField(fieldId).block()

        then: "the ticket field matches"
        noExceptionThrown()
        shown != null
        shown.getTicketField() != null
        shown.getTicketField().getId() == fieldId

        when: "deleting the ticket field"
        ticketsAdminClient.deleteTicketField(fieldId).block()

        then: "deletion succeeds without exception"
        noExceptionThrown()
    }

    def "fetching a ticket queries all custom fields preserving unset fields as Raw without null elements"() {
        given: "a custom ticket field created in the sandbox"
        String entropy = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        String customVal = "z4j-val-${entropy}"
        TicketField field = new TicketField("z4j-cf-${entropy}", TicketFieldTypeEnum.TEXT.getValue())
        TicketFieldResponse createdField = ticketsAdminClient.createTicketField(new TicketFieldCreateRequest(field)).block()
        Long fieldId = createdField.getTicketField().getId()

        and: "a ticket created with this custom field populated"
        TicketComment comment = new TicketComment().setBody("Testing custom fields ${entropy}")
        TicketCreateInput ticketInput = new TicketCreateInput(comment)
                .setRawSubject("Ticket with custom field ${entropy}")
        ticketInput.addCustomFieldsItem(new TicketCustomField.Text(fieldId, customVal))
        TicketResponse createdTicket = ticketsAdminClient.createTicket(new TicketCreateRequest(ticketInput)).block()
        Long ticketId = createdTicket.getTicket().getId()

        when: "fetching the ticket via showTicket"
        TicketResponse fetched = ticketsAgentClient.showTicket(ticketId).block()

        then: "the ticket is returned and custom fields are present"
        noExceptionThrown()
        fetched != null
        fetched.getTicket() != null
        List<TicketCustomField> customFields = fetched.getTicket().getCustomFields()
        customFields != null
        !customFields.isEmpty()

        and: "the custom fields list contains NO null elements"
        !customFields.any { it == null }

        and: "all custom field entries have valid IDs"
        customFields.every { it.id() != null }

        and: "the populated custom field is deserialized as a Text record with expected value"
        TicketCustomField populatedField = customFields.find { it.id() == fieldId }
        populatedField != null
        populatedField instanceof TicketCustomField.Text
        ((TicketCustomField.Text) populatedField).value() == customVal

        and: "unpopulated custom fields with null values are preserved as Raw records"
        List<TicketCustomField> nullValuedFields = customFields.findAll { it.value() == null }
        !nullValuedFields.isEmpty()
        nullValuedFields.every { it instanceof TicketCustomField.Raw }

        cleanup: "delete the test custom field from the sandbox"
        try {
            if (fieldId != null) {
                ticketsAdminClient.deleteTicketField(fieldId).block()
            }
        } catch (Exception ignored) {
        }
    }

    def "can update many tickets by ids as agent"() {
        given: "an existing ticket in the sandbox"
        Long ticketId = tickets.first().getId()
        String entropy = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        TicketUpdateInput updateInput = new TicketUpdateInput()
                .setComment(new TicketComment().setBody("Bulk update test ${entropy}").setIsPublic(false))
        TicketUpdateRequest updateRequest = new TicketUpdateRequest().setTicket(updateInput)

        when: "calling updateManyTickets"
        JobStatusResponse response = ticketsAgentClient.updateManyTickets(ticketId.toString(), updateRequest).block()

        then: "job status response is returned"
        noExceptionThrown()
        response != null
        response.getJobStatus() != null
        response.getJobStatus().getId() != null
        response.getJobStatus().getStatus() != null
    }
}
