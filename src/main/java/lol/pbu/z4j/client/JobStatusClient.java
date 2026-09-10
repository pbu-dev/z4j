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
package lol.pbu.z4j.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Retryable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lol.pbu.z4j.model.JobStatusResponse;
import reactor.core.publisher.Mono;

/**
 * <h1>Work with Background Job Statuses in Zendesk.</h1>
 * <ul>
 *     <li>Show Job Status {@link #showJobStatus}</li>
 * </ul>
 *
 * @since 0.2.3
 */
@Retryable
@Client("zendesk")
public interface JobStatusClient {

    /**
     * <h1>{@summary Show Job Status}</h1>
     * <p>Returns the status of a background job.</p>
     * <h4>Allowed For</h4> <ul> <li>Agents</li> </ul>
     *
     * @param jobStatusId The ID of the job status (required)
     * @return Job status (status code 200)
     */
    @Get("/api/v2/job_statuses/{job_status_id}")
    Mono<@Valid JobStatusResponse> showJobStatus(@PathVariable("job_status_id") @NotNull String jobStatusId);
}
