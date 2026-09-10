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
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import lol.pbu.z4j.Z4jSpec
import lol.pbu.z4j.model.*
import spock.lang.Shared

@MicronautTest
class JobStatusClientSpec extends Z4jSpec {

    @Shared JobStatusClient adminJobStatusClient, agentJobStatusClient, badUrlJobStatusClient
    @Shared TicketClient ticketClient
    @Shared String jobId

    def setupSpec() {
        adminJobStatusClient = adminCtx.getBean(JobStatusClient.class)
        agentJobStatusClient = agentCtx.getBean(JobStatusClient.class)
        badUrlJobStatusClient = badUrlCtx.getBean(JobStatusClient.class)
        ticketClient = agentCtx.getBean(TicketClient.class)

        // Trigger a batch update to get a real JobStatus ID
        def tickets = ticketClient.listTickets(null).block().getTickets()
        if (tickets != null && !tickets.isEmpty()) {
            Long ticketId = tickets.first().getId()
            TicketUpdateInput updateInput = new TicketUpdateInput()
                    .setComment(new TicketComment().setBody("Job status setup test").setIsPublic(false))
            TicketUpdateRequest updateRequest = new TicketUpdateRequest().setTicket(updateInput)
            JobStatusResponse response = ticketClient.updateManyTickets(ticketId.toString(), updateRequest).block()
            jobId = response?.getJobStatus()?.getId()
        }
    }

    def "can show job status by id as agent"() {
        given: "a valid job ID"
        assert jobId != null

        when: "fetching the job status"
        JobStatusResponse response = agentJobStatusClient.showJobStatus(jobId).block()

        then: "job status information is returned"
        noExceptionThrown()
        response != null
        response.getJobStatus() != null
        response.getJobStatus().getId() == jobId
        response.getJobStatus().getStatus() != null
    }

    def "fetching job status fails with bad url client"() {
        when: "calling showJobStatus with bad url"
        badUrlJobStatusClient.showJobStatus("fake-id").block()

        then: "HttpClientException is thrown"
        thrown(HttpClientException)
    }
}
