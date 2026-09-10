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
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Retryable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lol.pbu.z4j.model.*;
import reactor.core.publisher.Mono;

/**
 * <h1>Work with Tickets in Zendesk.</h1>
 * <ul>
 *     <li>Create Ticket {@link #createTicket}</li>
 *     <li>Create Custom Ticket Field {@link #createTicketField}</li>
 *     <li>Count Tickets {@link #getTicketCount}</li>
 *     <li>List Ticket Fields {@link #listTicketFields}</li>
 *     <li>List Tickets {@link #listTickets}</li>
 *     <li>Show Ticket {@link #showTicket}</li>
 *     <li>Update Ticket {@link #updateTicket}</li>
 * </ul>
 * @since 0.1.1
 * @author Jonathan-Zollinger
 */
@Retryable
@Client("zendesk")
public interface TicketClient {

    /**
     * <h1>{@summary Create Ticket}</h1>
     *
     * @param ticketCreateRequest (required)
     * @return Create ticket (status code 201)
     */
    @Post("/api/v2/tickets")
    Mono<@Valid TicketResponse> createTicket(@Body @NotNull @Valid TicketCreateRequest ticketCreateRequest);

    /**
     * <h1>{@summary Create Custom Ticket Field}</h1>
     * See{@link TicketFieldTypeEnum} and <a href="https://support.zendesk.com/hc/articles/4408883152794">Adding custom ticket fields to your tickets and forms</a>
     <p>See <a
     *         href='https://support.zendesk.com/hc/en-us/articles/203661866'>About custom field types</a> in the Zendesk Help
     *     Center.</p> <h4 id=\"allowed-for'>Allowed For</h4>
     * <ul>
     *     <li>Admins</li>
     * </ul> <h4 id=\"field-limits'>Field limits</h4> <p>We recommend the following best practices for ticket fields limits.
     *     Creating more than these amounts can affect performance.</p>
     * <ul>
     *     <li>400 ticket fields per account if your account doesn&#39;t have ticket forms</li>
     *     <li>400 ticket fields per ticket form if your account has ticket forms</li>
     * </ul>
     *
     * @return Created response (status code 201)
     */
    @Post("/api/v2/ticket_fields")
    Mono<@Valid TicketFieldResponse> createTicketField(@Body @NotNull @Valid TicketFieldCreateRequest ticketFieldCreateRequest);

    /**
     * <h1>{@summary Count Tickets}</h1>
     * Returns an approximate count of tickets in the account. If the count exceeds 100,000, it is updated every 24 hours. <br><br> {@code ccd} lists tickets that the specified user is cc'd on. <br><br> The {@code count[refreshed_at]} property is a timestamp that indicates when the count was last updated. <br><br> <strong>Note</strong>: When the count exceeds 100,000, {@code count[refreshed_at]} may occasionally be null. This indicates that the count is being updated in the background, and {count[value]} is limited to 100,000 until the update is complete.  <h4>Allowed For</h4> <ul>   <li>Agents</li> </ul>
     *
     * @return Count of tickets (status code 200)
     */
    @Get("/api/v2/tickets/count")
    Mono<@Valid TicketCountResponse> getTicketCount();

    /**
     * <h1>{@summary List Ticket Fields}</h1>
     * <p>Returns a list of all system and custom ticket fields in your account.</p> <p>For end users, only the ticket fields with visible_in_portal set to true are returned.</p> <p>Consider caching this resource to use with the{@link TicketClient}.</p> <h4 id=\"allowed-for'>Allowed For</h4> <ul>     <li>Anyone</li> </ul>
     *
     * @param localeAbbreviation  Forces the {@code titleInPortal} property to return a dynamic content variant for the specified locale. Only accepts {@link LocaleClient#listLocales() active locale ids}.  (optional)
     * @param creator Includes the {@code creatorUserId} and {@code creatorAppName} properties in the response. If the ticket field is created  by an app, {@code creatorAppName} is the name of the app and {@code creatorUserId} is {@code -1}. If the ticket field  is not created by an app, {@code creatorAppName} is null.  (optional)
     * @param pageAfter Cursor token used to fetch the next page when cursor pagination is active (optional)
     * @param pageSize  Number of results per page. Required to activate cursor pagination. Max 100. (optional)
     * @return Success response (status code 200)
     */
    default Mono<@Valid TicketFieldsResponse> listTicketFields(@QueryValue("locale") @Nullable LocaleAbbreviation localeAbbreviation, @QueryValue("creator") @Nullable Boolean creator) {
        return listTicketFields(localeAbbreviation, creator, null, null);
    }

    @Get("/api/v2/ticket_fields")
    Mono<@Valid TicketFieldsResponse> listTicketFields(
            @QueryValue("locale") @Nullable LocaleAbbreviation localeAbbreviation,
            @QueryValue("creator") @Nullable Boolean creator,
            @QueryValue("page[after]") @Nullable String pageAfter,
            @QueryValue("page[size]") @Nullable @Max(100) Integer pageSize
    );

    /**
     * <h1>{@summary Show Ticket Field}</h1>
     * <p>Returns the ticket field with the specified id.</p>
     * <h4>Allowed For</h4> <ul> <li>Agents</li> </ul>
     *
     * @param ticketFieldId The ID of the ticket field (required)
     * @return Success response (status code 200)
     */
    @Get("/api/v2/ticket_fields/{ticket_field_id}")
    Mono<@Valid TicketFieldResponse> showTicketField(@PathVariable("ticket_field_id") @NotNull Long ticketFieldId);

    /**
     * <h1>{@summary Delete Ticket Field}</h1>
     * <p>Deletes the ticket field with the specified id.</p>
     * <h4>Allowed For</h4> <ul> <li>Admins</li> </ul>
     *
     * @param ticketFieldId The ID of the ticket field (required)
     * @return No content response (status code 204)
     */
    @Delete("/api/v2/ticket_fields/{ticket_field_id}")
    Mono<Void> deleteTicketField(@PathVariable("ticket_field_id") @NotNull Long ticketFieldId);

    /**
     * <h1>{@summary List Tickets}</h1>
     *
     * @param externalId Lists tickets by external id. External ids don't have to be unique for each ticket. As a result, the request may return multiple tickets with the same external id. (optional)
     * @return List tickets (status code 200)
     */
    @Get("/api/v2/tickets")
    Mono<@Valid TicketsResponse> listTickets(@QueryValue("external_id") @Nullable String externalId);

    /**
     * <h1>{@summary Show Ticket}</h1>
     * <p>Returns a number of ticket properties though not the ticket comments. To get the comments, use <a href='developer.zendesk.com/api-reference/ticketing/tickets/ticket_comments/#list-comments'>List Comments</a></p> <h4 id=\"allowed-for'>Allowed For</h4> <ul>     <li>Agents</li> </ul>
     *
     * @param ticketId The ID of the ticket (required)
     * @return Ticket (status code 200)
     */
    @Get("/api/v2/tickets/{ticket_id}")
    Mono<@Valid TicketResponse> showTicket(@PathVariable("ticket_id") @NotNull Long ticketId);

    /**
     * <h1>{@summary Update Ticket}</h1>
     *
     * @param ticketId            The ID of the ticket (required)
     * @param ticketUpdateRequest (optional)
     * @return Successful request (status code 200)
     */
    @Put("/api/v2/tickets/{ticket_id}")
    Mono<@Valid TicketUpdateResponse> updateTicket(@PathVariable("ticket_id") @NotNull Long ticketId, @Body @Nullable @Valid TicketUpdateRequest ticketUpdateRequest);

    /**
     * <h1>{@summary Update Many Tickets}</h1>
     * <p>Updates multiple tickets identified by comma-separated IDs.</p>
     * <h4>Allowed For</h4> <ul> <li>Agents</li> </ul>
     *
     * @param ids Comma-separated list of ticket IDs (required)
     * @param ticketUpdateRequest Ticket update parameters (required)
     * @return Job status response (status code 200)
     */
    @Put("/api/v2/tickets/update_many")
    Mono<@Valid JobStatusResponse> updateManyTickets(
            @QueryValue("ids") @NotNull String ids,
            @Body @NotNull @Valid TicketUpdateRequest ticketUpdateRequest
    );

    /**
     * <h1>{@summary List Audits for a Ticket}</h1>
     * <p>Lists the audits for a specified ticket, showing all changes, comments, notifications,
     * and trigger/rule executions (via {@code via.source.from.title} and {@code via.source.rel: "trigger"}).</p>
     * <h4>Allowed For</h4> <ul> <li>Agents</li> </ul>
     *
     * @param ticketId The ID of the ticket (required)
     * @return Audits response containing ticket audit history (status code 200)
     */
    @Get("/api/v2/tickets/{ticket_id}/audits")
    Mono<@Valid TicketAuditsResponse> listAuditsForTicket(
            @PathVariable("ticket_id") @NotNull Long ticketId
    );
}
