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

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.ClientFilter;
import io.micronaut.http.annotation.RequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Base64.getEncoder;

/**
 * Add a basic auth to anything
 *
 * @author Jonathan Zollinger
 * @since 0.0.1
 */
@ClientFilter("/**")
@Requires(property = "micronaut.http.services.zendesk.email")
@Requires(property = "micronaut.http.services.zendesk.token")
public class BasicAuthFilter {

    private final String email;
    private final String token;

    private final Logger log = LoggerFactory.getLogger(BasicAuthFilter.class);

    /**
     * Constructor which pulls data from properties file or environment variables
     *
     * @param email user or admin email address
     * @param token api token generated from z4j
     */
    public BasicAuthFilter(@Property(name = "micronaut.http.services.zendesk.email") String email,
                           @Property(name = "micronaut.http.services.zendesk.token") String token) {
        this.email = email;
        this.token = token;
    }

    @RequestFilter
    public void doFilter(MutableHttpRequest<?> request) {
        String credentialsString = String.format("%s/token:%s", email, token);
        log.debug("Add basic auth header: Basic {}/token:<token>", email);
        request.header("Authorization", "Basic " + getEncoder().encodeToString(credentialsString.getBytes()));
        if (request.getContentType().isEmpty()) {
            request.header("Content-Type", "application/json");
        }
    }

}
