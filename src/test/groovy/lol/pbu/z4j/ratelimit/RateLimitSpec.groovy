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
package lol.pbu.z4j.ratelimit

import lol.pbu.z4j.Z4jSpec
import lol.pbu.z4j.client.SearchClient
import spock.lang.Shared

import java.time.Instant

class RateLimitSpec extends Z4jSpec {

    @Shared
    SearchClient searchClient

    @Shared
    RateLimitTracker tracker

    void setupSpec() {
        searchClient = adminCtx.getBean(SearchClient.class)
        tracker = adminCtx.getBean(RateLimitTracker.class)
    }

    def "EndpointRateLimit can parse Zendesk rate limit header correctly"() {
        when: "parsing a search index rate limit header"
        def limit = EndpointRateLimit.parse("zendesk-ratelimit-search-index", "total=2500; remaining=2499; resets=30")

        then:
        limit != null
        limit.name == "search-index"
        limit.total == 2500L
        limit.remaining == 2499L
        limit.resets == 30
    }

    def "EndpointRateLimit handles header with tickets-index"() {
        when: "parsing tickets-index header"
        def limit = EndpointRateLimit.parse("zendesk-ratelimit-tickets-index", "total=100000; remaining=99999; resets=35")

        then:
        limit != null
        limit.name == "tickets-index"
        limit.total == 100000L
        limit.remaining == 99999L
        limit.resets == 35
    }

    def "RateLimitSnapshot evaluates rate limiting thresholds correctly"() {
        given: "a normal snapshot"
        def normal = RateLimitSnapshot.builder()
                .statusCode(200)
                .globalLimit(700)
                .globalRemaining(650)
                .globalResetSeconds(45)
                .endpointLimits(["search-index": EndpointRateLimit.builder().name("search-index").remaining(2400L).build()])
                .timestamp(Instant.now())
                .build()

        and: "a snapshot near the rate limit"
        def nearLimit = RateLimitSnapshot.builder()
                .statusCode(200)
                .globalLimit(700)
                .globalRemaining(10)
                .globalResetSeconds(15)
                .endpointLimits(["search-index": EndpointRateLimit.builder().name("search-index").remaining(5L).build()])
                .timestamp(Instant.now())
                .build()

        and: "a 429 throttled snapshot"
        def throttled = RateLimitSnapshot.builder()
                .statusCode(429)
                .retryAfterSeconds(60)
                .timestamp(Instant.now())
                .build()

        expect:
        !normal.isRateLimited()
        !normal.isApproachingLimit(50)
        normal.getEndpointLimit("search-index").isPresent()
        normal.getEndpointLimit("zendesk-ratelimit-search-index").isPresent()

        !nearLimit.isRateLimited()
        nearLimit.isApproachingLimit(50)

        throttled.isRateLimited()
        throttled.retryAfterSeconds == 60
    }

    def "RateLimitTracker notifies registered listeners upon receiving snapshots"() {
        given: "a custom listener"
        RateLimitSnapshot captured = null
        RateLimitListener listener = { s -> captured = s }
        tracker.addListener(listener)

        when: "recording a snapshot"
        def snapshot = RateLimitSnapshot.builder()
                .requestMethod("GET")
                .requestPath("/api/v2/search")
                .statusCode(200)
                .globalLimit(700)
                .globalRemaining(690)
                .globalResetSeconds(25)
                .build()
        tracker.record(snapshot)

        then: "the listener received the snapshot"
        captured != null
        captured.globalRemaining == 690
        tracker.getLatestSnapshot() == snapshot
        tracker.getGlobalRemaining() == 690

        cleanup:
        tracker.removeListener(listener)
    }

    def "Live sandbox API call updates RateLimitTracker with real headers"() {
        given: "a listener registered on the tracker"
        RateLimitSnapshot liveSnapshot = null
        RateLimitListener listener = { s -> liveSnapshot = s }
        tracker.addListener(listener)

        when: "making a live search count call against Zendesk sandbox"
        searchClient.count("type:ticket").block()

        then: "rate limit headers are intercepted and recorded"
        liveSnapshot != null
        liveSnapshot.statusCode == 200
        liveSnapshot.globalRemaining != null
        liveSnapshot.globalRemaining > 0
        liveSnapshot.globalLimit != null
        liveSnapshot.globalLimit > 0

        and: "tracker state is updated"
        tracker.getLatestSnapshot() != null
        tracker.getGlobalRemaining() != null

        cleanup:
        tracker.removeListener(listener)
    }
}
