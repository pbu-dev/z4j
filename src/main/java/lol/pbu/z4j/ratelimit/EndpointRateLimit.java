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
package lol.pbu.z4j.ratelimit;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Represents endpoint-specific rate limits returned by Zendesk
 * in headers like {@code zendesk-ratelimit-search-index: total=2500; remaining=2499; resets=30}.
 *
 * @author Jonathan-Zollinger
 * @since 0.2.3
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Serdeable
public class EndpointRateLimit implements Serializable {

    public static final String PREFIX = "zendesk-ratelimit-";

    private String name;
    @Nullable private Long total;
    @Nullable private Long remaining;
    @Nullable private Integer resets;
    @Nullable private String rawHeader;

    /**
     * Parses a Zendesk endpoint rate limit header.
     *
     * @param headerName  The header name (e.g. {@code zendesk-ratelimit-search-index})
     * @param headerValue Semicolon-delimited values (e.g. {@code total=2500; remaining=2499; resets=30})
     * @return Parsed {@link EndpointRateLimit}
     */
    public static EndpointRateLimit parse(String headerName, String headerValue) {
        String cleanName = headerName.toLowerCase();
        if (cleanName.startsWith(PREFIX)) {
            cleanName = cleanName.substring(PREFIX.length());
        }

        EndpointRateLimitBuilder builder = EndpointRateLimit.builder()
                .name(cleanName)
                .rawHeader(headerValue);

        if (headerValue != null) {
            String[] parts = headerValue.split(";");
            for (String part : parts) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim().toLowerCase();
                    String val = kv[1].trim();
                    try {
                        switch (key) {
                            case "total" -> builder.total(Long.parseLong(val));
                            case "remaining" -> builder.remaining(Long.parseLong(val));
                            case "resets" -> builder.resets(Integer.parseInt(val));
                            default -> { }
                        }
                    } catch (NumberFormatException ignored) {
                        // ignore malformed numeric values
                    }
                }
            }
        }

        return builder.build();
    }
}
