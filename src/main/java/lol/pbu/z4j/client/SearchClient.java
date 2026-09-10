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

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Retryable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lol.pbu.z4j.model.*;
import reactor.core.publisher.Mono;

/**
 * <h1>{@summary Zendesk Search API}</h1>
 * <p>Reactive client for performing searches in Zendesk. These methods provide low-level access to the search endpoints.</p>
 * <ul>
 *     <li>Show Search Result Counts with {@link #count(String)}</li>
 *     <li>Export Search Results with {@link #exportTicket(String, Integer, String)}</li>
 *     <li>List Search Results with{@link #list}</li>
 * </ul>
 *
 * @author Jonathan-Zollinger
 * @since 0.1.1
 */
@Retryable
@Client("zendesk")
public interface SearchClient {

    /**
     * <h1>{@summary Show Search Results Count}</h1>
     * Returns the number of items matching the query rather than returning the items. The search string works the same as a regular search. <h4>Allowed For Agents</h4>
     *
     * @param query Returns the search results. See <a href='https://developer.zendesk.com/api-reference/ticketing/ticket-management/search/#query-syntax'>Query syntax</a> for details on the {@code query} parameter. For details on the query syntax, see the <a href='https://support.zendesk.com/hc/en-us/articles/203663226'>Zendesk Support search reference</a>. (required)
     * @return Success response (status code 200)
     * or Error response (status code 400)
     */
    @Get("/api/v2/search/count")
    Mono<@Valid SearchResponse> count(@QueryValue("query") @NotNull String query);

    /**
     * <h1>{@summary Export Search Results}</h1>
     * <p>Exports a set of Tickets. See <a
     * href='https://developer.zendesk.com/api-reference/ticketing/ticket-management/search/#query-syntax'>Query
     * syntax</a> for the syntax of the {@code query} parameter.</p> <p>Use this endpoint for search queries that will
     * return more than 1000 results. The result set is ordered only by the {@code created_at} attribute.</p> <p>The search
     * only returns results of a single object type. The following object types are supported: ticket, organization, user,
     * or group.</p> <p>You must specify the type in the {@code filter[type]} parameter. Searches with type in the query
     * string will result in an error.</p> Allowed for Agents <h4>Pagination</h4>
     * <ul>
     *     <li>Cursor
     *         pagination
     *     </li>
     * </ul> <p>See <a href='https://developer.zendesk.com/api-reference/introduction/pagination/'>Pagination</a>.</p> <p>
     *     Returns a maximum of 1000 records per page. The number of results shown in a page is determined by the {@code
     *     page[size]} parameter.</p> <p><strong>Note</strong>: You may experience a speed reduction or a timeout if you
     *     request 1000 results per page and you have many archived tickets in the results. Try reducing the number of results
     *     per page. We recommend 100 results per page.</p> <p>The cursor specified by the {@code after_cursor} property in a
     *     response expires after one hour.</p> <p>For more information on cursor-based pagination, see the following
     *     articles:</p>
     * <ul>
     *     <li>
     *         <a href='https://developer.zendesk.com/documentation/developer-tools/pagination/comparing-cursor-pagination-and-offset-pagination'>Comparing
     *             cursor pagination and offset pagination</a></li>
     *     <li>
     *         <a href='https://developer.zendesk.com/documentation/developer-tools/pagination/paginating-through-lists-using-cursor-pagination'>Paginating
     *             through lists using cursor pagination</a></li>
     * </ul> <h4>Export Search Results Limits</h4> <p>This API endpoint is rate-limited to 100 requests per minute per account.
     *     The limit also counts towards the global API rate
     *     limit.</p>
     *
     * @param query      Returns the search results. See <a href='https://developer.zendesk.com/api-reference/ticketing/ticket-management/search/#query-syntax'>Query syntax</a> for details on the {@code query} parameter. For details on the query syntax, see the <a href='https://support.zendesk.com/hc/en-us/articles/203663226'>Zendesk Support search reference</a>. (required)
     * @param pageSize   The number of results shown in a page. (required)
     * @param pageAfter  The cursor token for fetching the next page of results. (required)
     * @return Success response (status code 200)
     * or Error response (status code 400)
     */
    @Get("/api/v2/search/export?filter[type]=ticket")
    Mono<@Valid ExportResponse<Ticket>> exportTicket(
            @QueryValue("query") @NotNull String query,
            @QueryValue("page[size]") @Nullable @Max(1000) Integer pageSize,
            @QueryValue("page[after]") @Nullable String pageAfter
    );

    /**
     * <h1>{@summary List Search Results}</h1>
     * <p>Returns the search results. See <a
     * href='https://developer.zendesk.com/api-reference/ticketing/ticket-management/search/#query-syntax'>Query
     * syntax</a> for details on the {@code query} parameter.</p>
     *
     * <p>This endpoint has its own rate limit. The rate limit counts towards the global API rate
     * limit. See <a href='https://developer.zendesk.com/api-reference/ticketing/introduction/#limits'>Limits</a>.</p><h4>
     * Allowed For Agents</h4>
     *
     * @param query     Returns the search results. See <a href='https://developer.zendesk.com/api-reference/ticketing/ticket-management/search/#query-syntax'>Query syntax</a> for details on the {@code query} parameter. For details on the query syntax, see the <a href='https://support.zendesk.com/hc/en-us/articles/203663226'>Zendesk Support search reference</a>. (required)
     * @param include   Sideload related resources (e.g. {@code "users,organizations,groups"} or {@code "tickets(users,organizations)"}) (optional)
     * @param sortBy    One of {@code updated_at}, {@code created_at}, {@code priority}, {@code status}, or {@code ticket_type}. Defaults to sorting by relevance (optional)
     * @param sortOrder Defaults to descending (optional)
     * @param page      The page number to retrieve. (optional)
     * @param perPage   The count of results to include in each page. (optional)
     * @return Success response (status code 200)
     * or Error response (status code 400)
     */
    @Get("/api/v2/search")
    Mono<@Valid SearchResponse> list(
            @QueryValue("query") @NotNull String query,
            @QueryValue("include") @Nullable String include,
            @QueryValue("sort_by") @Nullable SortBy sortBy,
            @QueryValue("sort_order") @Nullable SortOrder sortOrder,
            @QueryValue("page") @Nullable Integer page,
            @QueryValue("per_page") @Nullable @Max(100) Integer perPage
    );

    /**
     * Overload for {@link #list(String, String, SortBy, SortOrder, Integer, Integer)} without sideloading.
     */
    default Mono<@Valid SearchResponse> list(
            String query,
            SortBy sortBy,
            SortOrder sortOrder,
            Integer page,
            Integer perPage
    ) {
        return list(query, null, sortBy, sortOrder, page, perPage);
    }
}
