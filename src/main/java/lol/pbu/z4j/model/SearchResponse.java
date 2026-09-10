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

import java.util.List;

/**
 * SearchResponse
 * @author Jonathan-Zollinger
 * @since 0.1.1
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonPropertyOrder({
        SearchResponse.JSON_PROPERTY_COUNT,
        SearchResponse.JSON_PROPERTY_FACETS,
        SearchResponse.JSON_PROPERTY_NEXT_PAGE,
        SearchResponse.JSON_PROPERTY_PREVIOUS_PAGE,
        SearchResponse.JSON_PROPERTY_RESULTS,
        SearchResponse.JSON_PROPERTY_USERS,
        SearchResponse.JSON_PROPERTY_ORGANIZATIONS,
        SearchResponse.JSON_PROPERTY_GROUPS,
})
@Serdeable
public class SearchResponse {

    public static final String JSON_PROPERTY_COUNT = "count";
    public static final String JSON_PROPERTY_FACETS = "facets";
    public static final String JSON_PROPERTY_NEXT_PAGE = "next_page";
    public static final String JSON_PROPERTY_PREVIOUS_PAGE = "previous_page";
    public static final String JSON_PROPERTY_RESULTS = "results";
    public static final String JSON_PROPERTY_USERS = "users";
    public static final String JSON_PROPERTY_ORGANIZATIONS = "organizations";
    public static final String JSON_PROPERTY_GROUPS = "groups";

    /**
     * The number of resources returned by the query corresponding to this page of results in the paginated response
     */
    @Nullable
    @JsonProperty(JSON_PROPERTY_COUNT)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private Integer count;

    /**
     * The facets corresponding to the search query
     */
    @Nullable
    @JsonProperty(JSON_PROPERTY_FACETS)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String facets;

    /**
     * URL to the next page of results
     */
    @Nullable
    @JsonProperty(JSON_PROPERTY_NEXT_PAGE)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String nextPage;

    /**
     * URL to the previous page of results
     */
    @Nullable
    @JsonProperty(JSON_PROPERTY_PREVIOUS_PAGE)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String previousPage;

    /**
     * May consist of tickets, users, groups, or organizations, as specified by the `result_type` property in each result object
     */
    @Nullable
    @JsonProperty(JSON_PROPERTY_RESULTS)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private List<@Valid SearchResult> results;

    /**
     * Sideloaded users associated with search results (when include=users is requested)
     */
    @Nullable
    @JsonProperty(JSON_PROPERTY_USERS)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private List<@Valid User> users;

    /**
     * Sideloaded organizations associated with search results (when include=organizations is requested)
     */
    @Nullable
    @JsonProperty(JSON_PROPERTY_ORGANIZATIONS)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private List<@Valid Organization> organizations;

    /**
     * Sideloaded groups associated with search results (when include=groups is requested)
     */
    @Nullable
    @JsonProperty(JSON_PROPERTY_GROUPS)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private List<@Valid Group> groups;

}
