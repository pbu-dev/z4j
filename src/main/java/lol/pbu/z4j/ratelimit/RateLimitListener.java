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

/**
 * Listener interface for observing Zendesk rate limit updates.
 * Implementations can be registered as Micronaut beans or added directly to {@link RateLimitTracker}.
 *
 * @author Jonathan-Zollinger
 * @since 0.2.3
 */
@FunctionalInterface
public interface RateLimitListener {

    /**
     * Invoked whenever a Zendesk HTTP response is intercepted and rate limit information is updated.
     *
     * @param snapshot The updated rate limit snapshot
     */
    void onRateLimitUpdate(RateLimitSnapshot snapshot);
}
