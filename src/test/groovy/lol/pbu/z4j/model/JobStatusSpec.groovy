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
package lol.pbu.z4j.model

import lol.pbu.z4j.Z4jSpec

class JobStatusSpec extends Z4jSpec {

    def "should instantiate and set properties on JobStatus"() {
        given:
        def job = new JobStatus()

        when:
        job.setId("job_123")
           .setUrl("https://example.zendesk.com/api/v2/job_statuses/job_123.json")
           .setTotal(5)
           .setProgress(3)
           .setStatus("working")
           .setMessage("Processing 3 of 5")
           .setJobType("ticket_bulk_update")
           .setResults([[id: 1L, action: "update", success: true]])

        then:
        job.id == "job_123"
        job.url == "https://example.zendesk.com/api/v2/job_statuses/job_123.json"
        job.total == 5
        job.progress == 3
        job.status == "working"
        job.message == "Processing 3 of 5"
        job.jobType == "ticket_bulk_update"
        job.results.size() == 1
        job.results[0].success == true
    }

    def "should instantiate JobStatusResponse"() {
        given:
        def job = new JobStatus().setId("job_abc").setStatus("completed")

        when:
        def response = new JobStatusResponse(job)

        then:
        response.jobStatus != null
        response.jobStatus.id == "job_abc"
        response.jobStatus.status == "completed"
    }
}
