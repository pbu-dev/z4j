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
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * AuditEventsInner
 *
 * @author Jonathan-Zollinger
 * @since 0.1.1
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonPropertyOrder({
        AuditEventsInner.JSON_PROPERTY_BODY,
        AuditEventsInner.JSON_PROPERTY_FIELD_NAME,
        AuditEventsInner.JSON_PROPERTY_ID,
        AuditEventsInner.JSON_PROPERTY_TYPE,
        AuditEventsInner.JSON_PROPERTY_VALUE,
        AuditEventsInner.JSON_PROPERTY_PREVIOUS_VALUE,
        AuditEventsInner.JSON_PROPERTY_VIA,
        AuditEventsInner.JSON_PROPERTY_SUBJECT,
        AuditEventsInner.JSON_PROPERTY_RECIPIENTS,
})
@Serdeable
public class AuditEventsInner {

    public static final String JSON_PROPERTY_BODY = "body";
    public static final String JSON_PROPERTY_FIELD_NAME = "field_name";
    public static final String JSON_PROPERTY_ID = "id";
    public static final String JSON_PROPERTY_TYPE = "type";
    public static final String JSON_PROPERTY_VALUE = "value";
    public static final String JSON_PROPERTY_PREVIOUS_VALUE = "previous_value";
    public static final String JSON_PROPERTY_VIA = "via";
    public static final String JSON_PROPERTY_SUBJECT = "subject";
    public static final String JSON_PROPERTY_RECIPIENTS = "recipients";

    @Nullable
    @JsonProperty(JSON_PROPERTY_BODY)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String body;

    @Nullable
    @JsonProperty(JSON_PROPERTY_FIELD_NAME)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String fieldName;

    @Nullable
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Long id;

    @Nullable
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String type;

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Object value;

    @Nullable
    @JsonProperty(JSON_PROPERTY_PREVIOUS_VALUE)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Object previousValue;

    @Nullable
    @JsonProperty(JSON_PROPERTY_VIA)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Via via;

    @Nullable
    @JsonProperty(JSON_PROPERTY_SUBJECT)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String subject;

    @Nullable
    @JsonProperty(JSON_PROPERTY_RECIPIENTS)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private java.util.List<Long> recipients;

}
