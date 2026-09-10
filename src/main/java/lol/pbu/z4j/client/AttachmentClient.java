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

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Retryable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lol.pbu.z4j.model.AttachmentResponse;
import lol.pbu.z4j.model.AttachmentUploadResponse;
import reactor.core.publisher.Mono;

/**
 * <h1>Work with Attachments and Uploads in Zendesk.</h1>
 * <ul>
 *     <li>Upload Attachment {@link #uploadAttachment}</li>
 *     <li>Show Attachment {@link #showAttachment}</li>
 *     <li>Delete Attachment {@link #deleteAttachment}</li>
 * </ul>
 *
 * @since 0.2.3
 */
@Retryable
@Client("zendesk")
public interface AttachmentClient {

    /**
     * <h1>{@summary Upload Attachment File}</h1>
     * <p>Uploads a file to get an upload token. The token can then be used in a ticket comment to attach the file.</p>
     * <h4>Allowed For</h4> <ul> <li>Agents, End Users</li> </ul>
     *
     * @param filename The name of the file (required)
     * @param token Optional existing upload token to attach multiple files to a single token (optional)
     * @param contentType MIME type of the file, e.g. "image/png" or "application/octet-stream"
     * @param data Binary contents of the file
     * @return Upload response with token and attachment metadata (status code 201)
     */
    @Post(value = "/api/v2/uploads", consumes = MediaType.APPLICATION_JSON)
    Mono<@Valid AttachmentUploadResponse> uploadAttachment(
            @QueryValue("filename") @NotNull String filename,
            @QueryValue("token") @Nullable String token,
            @Header("Content-Type") @Nullable String contentType,
            @Body byte[] data
    );

    default Mono<@Valid AttachmentUploadResponse> uploadAttachment(
            String filename,
            String contentType,
            byte[] data
    ) {
        return uploadAttachment(filename, null, contentType, data);
    }

    /**
     * <h1>{@summary Show Attachment}</h1>
     * <p>Returns the attachment with the specified id.</p>
     * <h4>Allowed For</h4> <ul> <li>Agents</li> </ul>
     *
     * @param attachmentId The ID of the attachment (required)
     * @return Attachment (status code 200)
     */
    @Get("/api/v2/attachments/{attachment_id}")
    Mono<@Valid AttachmentResponse> showAttachment(@PathVariable("attachment_id") @NotNull Long attachmentId);

    /**
     * <h1>{@summary Delete Attachment}</h1>
     * <p>Redacts an attachment permanently from a ticket comment.</p>
     * <h4>Allowed For</h4> <ul> <li>Admins</li> </ul>
     *
     * @param attachmentId The ID of the attachment (required)
     * @return Void (status code 200/204)
     */
    @Delete("/api/v2/attachments/{attachment_id}")
    Mono<Void> deleteAttachment(@PathVariable("attachment_id") @NotNull Long attachmentId);
}
