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
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe singleton tracker for Zendesk rate limit states.
 * Collects rate limit metrics and notifies registered {@link RateLimitListener}s.
 *
 * @author Jonathan-Zollinger
 * @since 0.2.3
 */
@Singleton
public class RateLimitTracker {

    private static final Logger log = LoggerFactory.getLogger(RateLimitTracker.class);

    private final AtomicReference<RateLimitSnapshot> latestSnapshot = new AtomicReference<>();
    private final Map<String, EndpointRateLimit> endpointLimits = new ConcurrentHashMap<>();
    private final Map<String, RateLimitSnapshot> pathSnapshots = new ConcurrentHashMap<>();
    private final List<RateLimitListener> listeners = new CopyOnWriteArrayList<>();

    public RateLimitTracker() {
    }

    @Inject
    public RateLimitTracker(@Nullable List<RateLimitListener> injectedListeners) {
        if (injectedListeners != null) {
            this.listeners.addAll(injectedListeners);
        }
    }

    /**
     * Registers a rate limit listener.
     *
     * @param listener Listener to add
     * @return this tracker
     */
    public RateLimitTracker addListener(RateLimitListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
        return this;
    }

    /**
     * Unregisters a rate limit listener.
     *
     * @param listener Listener to remove
     * @return true if removed
     */
    public boolean removeListener(RateLimitListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Records a new rate limit snapshot and notifies listeners.
     *
     * @param snapshot Snapshot from an HTTP response
     */
    public void record(RateLimitSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }

        latestSnapshot.set(snapshot);

        if (snapshot.getRequestPath() != null) {
            pathSnapshots.put(snapshot.getRequestPath(), snapshot);
        }

        if (snapshot.getEndpointLimits() != null) {
            endpointLimits.putAll(snapshot.getEndpointLimits());
        }

        for (RateLimitListener listener : listeners) {
            try {
                listener.onRateLimitUpdate(snapshot);
            } catch (Exception e) {
                log.error("Error in RateLimitListener: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * @return The most recent rate limit snapshot, or null if no calls made yet
     */
    @Nullable
    public RateLimitSnapshot getLatestSnapshot() {
        return latestSnapshot.get();
    }

    /**
     * @return Unmodifiable map of all latest known endpoint-specific rate limits
     */
    public Map<String, EndpointRateLimit> getAllEndpointLimits() {
        return Collections.unmodifiableMap(endpointLimits);
    }

    /**
     * Retrieves the latest known limit for a specific endpoint (e.g. {@code "search-index"}).
     *
     * @param name Endpoint limit name
     * @return Optional containing the {@link EndpointRateLimit}
     */
    public Optional<EndpointRateLimit> getEndpointLimit(String name) {
        if (name == null) {
            return Optional.empty();
        }
        String clean = name.toLowerCase();
        if (clean.startsWith(EndpointRateLimit.PREFIX)) {
            clean = clean.substring(EndpointRateLimit.PREFIX.length());
        }
        return Optional.ofNullable(endpointLimits.get(clean));
    }

    /**
     * @return Latest global remaining request count, or null if unknown
     */
    @Nullable
    public Integer getGlobalRemaining() {
        RateLimitSnapshot snapshot = latestSnapshot.get();
        return snapshot != null ? snapshot.getGlobalRemaining() : null;
    }

    /**
     * Checks if any endpoint or global remaining quota is at or below the given threshold.
     *
     * @param threshold Warning threshold
     * @return true if approaching rate limit
     */
    public boolean isApproachingLimit(int threshold) {
        RateLimitSnapshot snapshot = latestSnapshot.get();
        if (snapshot != null && snapshot.isApproachingLimit(threshold)) {
            return true;
        }
        return endpointLimits.values().stream()
                .anyMatch(e -> e.getRemaining() != null && e.getRemaining() <= threshold);
    }
}
