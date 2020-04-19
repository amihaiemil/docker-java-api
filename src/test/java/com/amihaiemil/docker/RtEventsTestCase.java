/**
 * Copyright (c) 2018-2020, Mihai Emil Andronache
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1)Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2)Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3)Neither the name of docker-java-api nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.amihaiemil.docker;

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.JsonObject;
import java.net.URI;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Unit tests for {@link RtEvents}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
 */
public final class RtEventsTestCase {

    /**
     * RtEvents can monitor evens without filters.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void monitorsAll() throws Exception {
        final Events all = new RtEvents(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "{\"id\": \"eventId\"}"
                ),
                new Condition(
                    "monitor() must send a GET request",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "monitor() resource URL must be '/events'",
                    req -> req.getRequestLine()
                            .getUri().endsWith("/events")
                )
            ),
            URI.create("http://localhost/1.40/events"),
            Mockito.mock(Docker.class)
        );
        final List<JsonObject> events = all.monitor().limit(1)
            .collect(Collectors.toList());
        MatcherAssert.assertThat(events, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(
            events.get(0).getString("id"),
            Matchers.equalTo("eventId")
        );
    }

    /**
     * RtEvents can monitor evens with the Since filter.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void monitorsSince() throws Exception {
        final LocalDateTime since = LocalDateTime.parse("2020-04-01T00:00");
        final long sinceMillis = ZonedDateTime.of(
            since,
            ZoneId.systemDefault()
        ).toInstant().toEpochMilli();
        final Events all = new RtEvents(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "{\"id\": \"eventId\"}"
                ),
                new Condition(
                    "monitor() must send a GET request",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "monitor() resource URL must be '/events?since=...'",
                    req -> req.getRequestLine()
                        .getUri().endsWith("/events?since=" + sinceMillis)
                )
            ),
            URI.create("http://localhost/1.40/events"),
            Mockito.mock(Docker.class)
        ).since(since);
        final List<JsonObject> events = all.monitor().limit(1)
            .collect(Collectors.toList());
        MatcherAssert.assertThat(events, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(
            events.get(0).getString("id"),
            Matchers.equalTo("eventId")
        );
    }

    /**
     * RtEvents can monitor evens with the Until filter.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void monitorsUntil() throws Exception {
        final LocalDateTime until = LocalDateTime.parse("2020-06-01T00:00");
        final long untilMillis = ZonedDateTime.of(
            until,
            ZoneId.systemDefault()
        ).toInstant().toEpochMilli();
        final Events all = new RtEvents(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "{\"id\": \"eventId\"}"
                ),
                new Condition(
                    "monitor() must send a GET request",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "monitor() resource URL must be '/events?until=...'",
                    req -> req.getRequestLine()
                        .getUri().endsWith("/events?until=" + untilMillis)
                )
            ),
            URI.create("http://localhost/1.40/events"),
            Mockito.mock(Docker.class)
        ).until(until);
        final List<JsonObject> events = all.monitor().limit(1)
            .collect(Collectors.toList());
        MatcherAssert.assertThat(events, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(
            events.get(0).getString("id"),
            Matchers.equalTo("eventId")
        );
    }

    /**
     * RtEvents can monitor evens with the a filter map.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void monitorsWithFilter() throws Exception {
        final Events all = new RtEvents(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "{\"id\": \"eventId\"}"
                ),
                new Condition(
                    "monitor() must send a GET request",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "monitor() resource URL must be '/events?filters=...",
                    req -> URLDecoder.decode(
                        req.getRequestLine().getUri()
                    ).endsWith(
                        "/events?filters={\"type\":[\"container\"]}"
                    )
                )
            ),
            URI.create("http://localhost/1.40/events"),
            Mockito.mock(Docker.class)
        ).filter(
            () -> {
                final Map<String, Iterable<String>> filters = new HashMap<>();
                filters.put("type", Arrays.asList("container"));
                return filters;
            }
        );
        final List<JsonObject> events = all.monitor().limit(1)
            .collect(Collectors.toList());
        MatcherAssert.assertThat(events, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(
            events.get(0).getString("id"),
            Matchers.equalTo("eventId")
        );
    }

    /**
     * RtEvents can monitor evens with the a filter map and also the
     * Since/Until flags.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void monitorsWithSinceUntilAndFilters() throws Exception {
        final LocalDateTime since = LocalDateTime.parse("2020-04-01T00:00");
        final long sinceMillis = ZonedDateTime.of(
            since,
            ZoneId.systemDefault()
        ).toInstant().toEpochMilli();
        final LocalDateTime until = LocalDateTime.parse("2020-06-01T00:00");
        final long untilMillis = ZonedDateTime.of(
            until,
            ZoneId.systemDefault()
        ).toInstant().toEpochMilli();
        final Events all = new RtEvents(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "{\"id\": \"eventId\"}"
                ),
                new Condition(
                    "monitor() must send a GET request",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "monitor() resource URL must be "
                        + "'/events?since=...&until=...&filters=...",
                    req -> URLDecoder.decode(
                        req.getRequestLine().getUri()
                    ).endsWith(
                        "/events?since=" + sinceMillis
                      + "&until=" + untilMillis
                      + "&filters={\"type\":[\"container\"]}"
                    )
                )
            ),
            URI.create("http://localhost/1.40/events"),
            Mockito.mock(Docker.class)
        ).since(since).until(until).filter(
            () -> {
                final Map<String, Iterable<String>> filters = new HashMap<>();
                filters.put("type", Arrays.asList("container"));
                return filters;
            }
        );
        final List<JsonObject> events = all.monitor().limit(1)
            .collect(Collectors.toList());
        MatcherAssert.assertThat(events, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(
            events.get(0).getString("id"),
            Matchers.equalTo("eventId")
        );
    }
}
