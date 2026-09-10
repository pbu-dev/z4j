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
package lol.pbu.z4j.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Upload details including token and attachment objects.
 *
 * @since 0.2.3
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonPropertyOrder({
        "token",
        "attachment",
        "attachments"
})
@Serdeable
public class AttachmentUploadResponseUpload {

    @Nullable
    @JsonProperty("token")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String token;

    @Nullable
    @JsonProperty("attachment")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private AttachmentObject attachment;

    @Nullable
    @JsonProperty("attachments")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private List<AttachmentObject> attachments;

    public AttachmentUploadResponseUpload addAttachmentsItem(AttachmentObject attachmentsItem) {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(attachmentsItem);
        return this;
    }
}
