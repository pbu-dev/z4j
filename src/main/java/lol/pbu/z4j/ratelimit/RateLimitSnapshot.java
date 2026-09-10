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
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Snapshot of Zendesk rate limit state after an HTTP response.
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
public class RateLimitSnapshot implements Serializable {

    @Nullable private String requestMethod;
    @Nullable private String requestPath;
    @Nullable private String requestUri;
    private int statusCode;

    @Nullable private Integer globalLimit;
    @Nullable private Integer globalRemaining;
    @Nullable private Integer globalResetSeconds;

    @Nullable private Integer retryAfterSeconds;

    @Builder.Default
    private Map<String, EndpointRateLimit> endpointLimits = Collections.emptyMap();

    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * @return true if the response was an HTTP 429 Too Many Requests
     */
    public boolean isRateLimited() {
        return statusCode == 429;
    }

    /**
     * Checks if either the global quota or any endpoint quota has fallen to or below the given threshold.
     *
     * @param threshold Warning threshold
     * @return true if any remaining quota &lt;= threshold
     */
    public boolean isApproachingLimit(int threshold) {
        if (globalRemaining != null && globalRemaining <= threshold) {
            return true;
        }
        if (endpointLimits != null) {
            return endpointLimits.values().stream()
                    .anyMatch(e -> e.getRemaining() != null && e.getRemaining() <= threshold);
        }
        return false;
    }

    /**
     * Retrieves an endpoint-specific rate limit by its clean name (e.g. {@code "search-index"}).
     *
     * @param name Name of the endpoint limit
     * @return Optional containing the {@link EndpointRateLimit} if present
     */
    public Optional<EndpointRateLimit> getEndpointLimit(String name) {
        if (endpointLimits == null || name == null) {
            return Optional.empty();
        }
        String clean = name.toLowerCase();
        if (clean.startsWith(EndpointRateLimit.PREFIX)) {
            clean = clean.substring(EndpointRateLimit.PREFIX.length());
        }
        return Optional.ofNullable(endpointLimits.get(clean));
    }
}
