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

import java.util.List;

/**
 * A file represented as an Attachment object.
 *
 * @since 0.2.3
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonPropertyOrder({
        "id",
        "file_name",
        "content_url",
        "content_type",
        "size",
        "thumbnails",
        "inline",
        "deleted",
        "url",
        "width",
        "height",
        "malware_scan_result"
})
@Serdeable
public class AttachmentObject {

    @Nullable
    @JsonProperty("id")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Long id;

    @Nullable
    @JsonProperty("file_name")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String fileName;

    @Nullable
    @JsonProperty("content_url")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String contentUrl;

    @Nullable
    @JsonProperty("content_type")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String contentType;

    @Nullable
    @JsonProperty("size")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Long size;

    @Nullable
    @JsonProperty("thumbnails")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private List<AttachmentObject> thumbnails;

    @Nullable
    @JsonProperty("inline")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Boolean inline;

    @Nullable
    @JsonProperty("deleted")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Boolean deleted;

    @Nullable
    @JsonProperty("url")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String url;

    @Nullable
    @JsonProperty("width")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Long width;

    @Nullable
    @JsonProperty("height")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Long height;

    @Nullable
    @JsonProperty("malware_scan_result")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String malwareScanResult;
}
