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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * RESTful Events API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
 */
final class RtEvents implements Events {
    /**
     * Events filters.
     */
    private final Map<String, Iterable<String>> filters;

    /**
     * Since tim
     * estamp.
     */
    private final LocalDateTime since;

    /**
     * Until timestamp.
     */
    private final LocalDateTime until;

    /**
     * Apache HttpClient which sends the requests.
     */
    private final HttpClient client;

    /**
     * Base URI.
     */
    private final URI baseUri;

    /**
     * Docker API.
     */
    private final Docker docker;

    /**
     * Ctor.
     * @param client Given HTTP Client.
     * @param baseUri Base URI, ending with /containers.
     * @param dkr Docker where these Containers are from.
     */
    RtEvents(final HttpClient client, final URI baseUri, final Docker dkr) {
        this(client, baseUri, dkr, new HashMap<>(), null, null);
    }

    /**
     * Ctor.
     * @param client Given HTTP Client.
     * @param baseUri Base URI, ending with /containers.
     * @param dkr Docker where these Containers are from.
     * @param filters Filters to apply on these events.
     * @param since Since timestamp.
     * @param until Until timestamp.
     * @checkstyle ParameterNumber (2 lines)
     */
    RtEvents(
        final HttpClient client, final URI baseUri, final Docker dkr,
        final Map<String, Iterable<String>> filters,
        final LocalDateTime since,
        final LocalDateTime until
    ) {
        this.client = client;
        this.baseUri = baseUri;
        this.docker = dkr;
        this.filters = filters;
        this.since = since;
        this.until = until;
    }

    @Override
    public Events since(final LocalDateTime timestamp) {
        return new RtEvents(
            this.client,
            this.baseUri,
            this.docker,
            this.filters,
            timestamp,
            this.until
        );
    }

    @Override
    public Events until(final LocalDateTime timestamp) {
        return new RtEvents(
            this.client,
            this.baseUri,
            this.docker,
            this.filters,
            this.since,
            timestamp
        );
    }

    @Override
    public Events filter(
        final Supplier<Map<String, Iterable<String>>> filter
    ) {
        final Map<String, Iterable<String>> merged = new HashMap<>(
            this.filters
        );
        merged.putAll(filter.get());
        return new RtEvents(
            this.client,
            this.baseUri,
            this.docker,
            merged,
            this.since,
            this.until
        );
    }

    /**
     * Unlike other methods, we cannot implement this one using Response
     * Handlers, because Apache HTTP Client tries to consume the remaining
     * content after all the handlers have been executed, which results in
     * a blockage, since the underlying InputStream is potentially infinite.
     *
     * @checkstyle CommentsIndentation (100 lines)
     * @checkstyle Indentation (100 lines)
     * @return Stream of Events.
     * @throws IOException If any I/O problem occurs.
     * @throws UnexpectedResponseException If the response status is not 200.
     */
    @Override
    public Stream<JsonObject> monitor()
        throws IOException, UnexpectedResponseException {
        final HttpGet monitor = new HttpGet(this.buildMonitorUri());
        final HttpResponse response = this.client.execute(monitor);
        final int actual = response.getStatusLine().getStatusCode();
        if(actual != HttpStatus.SC_OK) {
            throw new UnexpectedResponseException(
                this.buildMonitorUri().toString(),
                actual, HttpStatus.SC_OK,
                Json.createObjectBuilder().build()
            );
        } else {
            final InputStream content = response.getEntity().getContent();
            final Stream<JsonObject> stream = Stream.generate(
                () -> {
                    JsonObject read = null;
                    try {
                        final byte[] tmp = new byte[4096];
                        while (content.read(tmp) != -1) {
                            try {
                                final JsonReader reader = Json.createReader(
                                    new ByteArrayInputStream(tmp)
                                );
                                read = reader.readObject();
                                break;
                            //@checkstyle IllegalCatch (1 line)
                            } catch (final Exception exception) {
                                //Couldn't parse byte[] to Json,
                                //try to read more bytes.
                            }
                        }
                    } catch (final IOException ex) {
                        throw new IllegalStateException(
                            "IOException when reading streamed JsonObjects!"
                        );
                    }
                    return read;
                }
            ).onClose(
                () -> {
                    try {
                        ((CloseableHttpResponse) response).close();
                    } catch (final IOException ex) {
                        //There is a bug in Apache HTTPClient, when closing
                        //an infinite InputStream: IOException is thrown
                        //because the client still tries to read the remainder
                        // of the closed Stream. We should ignore this case.
                    }
                }
            );
            return stream;
        }
    }

    @Override
    public Docker docker() {
        return this.docker;
    }

    /**
     * Build up the URI for the monitoring operation.
     * Add the since/until query params if they are present
     * and also add the filters.
     * @return URI.
     */
    private URI buildMonitorUri() {
        final URIBuilder uriBuilder = new UncheckedUriBuilder(
                this.baseUri.toString()
        );
        if (this.since != null) {
            uriBuilder.addParameter(
                "since",
                String.valueOf(
                    ZonedDateTime.of(
                        this.since, ZoneId.systemDefault()
                    ).toInstant().toEpochMilli()
                )
            );
        }
        if (this.until != null) {
            uriBuilder.addParameter(
                "until",
                String.valueOf(
                    ZonedDateTime.of(
                        this.until, ZoneId.systemDefault()
                    ).toInstant().toEpochMilli()
                )
            );
        }
        final FilteredUriBuilder uri = new FilteredUriBuilder(
            uriBuilder,
            this.filters
        );
        return uri.build();
    }
}
