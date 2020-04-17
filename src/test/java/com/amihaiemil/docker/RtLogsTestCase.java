/**
 * Copyright (c) 2018-2019, Mihai Emil Andronache
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
import java.io.BufferedReader;
import java.net.URI;
import java.util.stream.Collectors;
import javax.json.Json;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link RtLogs}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.2
 */
public final class RtLogsTestCase {
    
    /**
     * RtLogs can return the Container which owns them.
     */
    @Test
    public void getsContainer(){
        final Container owner = Mockito.mock(Container.class);
        final Logs logs = new RtLogs(
            owner,
            Mockito.mock(HttpClient.class),
            URI.create("http://localhost:8080/containers/123/logs")
        );
        MatcherAssert.assertThat(
            logs.container() == owner,
            Matchers.is(Boolean.TRUE)
        );
    }
    
    /**
     * RtLogs can follow the Container's logs (return a Reader of the stream).
     * @throws Exception If something goes wrong.
     */
    @Test
    public void followsLogs() throws Exception {
        final Logs logs = new RtLogs(
            Mockito.mock(Container.class),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createObjectBuilder()
                        .add("logs", "...some logs...")
                        .build().toString()
                ),
                new Condition(
                    "Method should be a GET",
                    req -> req.getRequestLine().getMethod().equals("GET")
                ),
                new Condition(
                    "Resource path must be /123/logs?follow=true",
                    req -> req.getRequestLine().getUri().endsWith(
                        "/123/logs?follow=true&stdout=true&stderr=true"
                    )
                )
            ),
            URI.create("http://localhost:80/1.30/containers/123/logs")
        );
        try (final BufferedReader bfr = new BufferedReader(logs.follow());) {
            MatcherAssert.assertThat(
                bfr.lines().collect(Collectors.joining("\n")),
                Matchers.equalTo("{\"logs\":\"...some logs...\"}")
            );
        }
    }

    /**
     * RtLogs can fetch the Container's logs (return them as a String).
     * @throws Exception If something goes wrong.
     */
    @Test
    public void fetchesLogs() throws Exception {
        final Logs logs = new RtLogs(
            Mockito.mock(Container.class),
            new AssertRequest(
                new Response(HttpStatus.SC_OK,
                    this.prepareMessage("...fetched logs...")),
                new Condition(
                    "Method should be a GET",
                    req -> req.getRequestLine().getMethod().equals("GET")
                ),
                new Condition(
                    "Resource path must be /123/logs",
                    req -> req.getRequestLine().getUri().endsWith(
                        "/123/logs?stdout=true&stderr=true"
                    )
                )
            ),
            URI.create("http://localhost:80/1.30/containers/123/logs")
        );
        MatcherAssert.assertThat(
            logs.fetch(),
            Matchers.equalTo("...fetched logs...")
        );
    }

    /**
     * Docker logs contains header -
     * header := [8]byte{STREAM_TYPE, 0, 0, 0, SIZE1, SIZE2, SIZE3, SIZE4}
     * STREAM_TYPE
     * 0: stdin (is written on stdout)
     * 1: stdout
     * 2: stderr
     *
     * SIZE1, SIZE2, SIZE3, SIZE4 are the four bytes of the uint32 size
     * encoded as big endian.
     *
     * @param message Message from container
     * @return String with header.
     */
    private String prepareMessage(final String message) {
        char[] chars = new char[8];
        chars[0] = 1;
        chars[1] = 0;
        chars[2] = 0;
        chars[3] = 0;
        chars[4] = 0;
        chars[5] = 0;
        chars[6] = 0;
        chars[7] = (char) message.length();
        return new String(chars) + message;
    }

    /**
     * RtLogs.toString() fetches the logs as String.
     */
    @Test
    public void toStringFetch() {
        final Logs logs = new RtLogs(
            Mockito.mock(Container.class),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    this.prepareMessage("toString logs")
                ),
                new Condition(
                    "Method should be a GET",
                    req -> req.getRequestLine().getMethod().equals("GET")
                ),
                new Condition(
                    "Resource path must be /123/logs",
                    req -> req.getRequestLine().getUri().endsWith(
                        "/123/logs?stdout=true&stderr=true"
                    )
                )
            ),
            URI.create("http://localhost:80/1.30/containers/123/logs")
        );
        MatcherAssert.assertThat(
            logs.toString(),
            Matchers.equalTo("toString logs")
        );
    }

    /**
     * RtLogs.stdout().fetch() fetches only stdout logs as String.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void logStdout() throws Exception {
        final Logs logs = new RtLogs(
            Mockito.mock(Container.class),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    this.prepareMessage("stdout logs")
                ),
                new Condition(
                    "Method should be a GET",
                    req -> req.getRequestLine().getMethod().equals("GET")
                ),
                new Condition(
                    "Resource path must be /123/logs",
                    req -> req.getRequestLine().getUri().endsWith(
                        "/123/logs?stdout=true&stderr=false"
                    )
                )
            ),
            URI.create("http://localhost:80/1.30/containers/123/logs")
        );
        MatcherAssert.assertThat(
            logs.stdout().fetch(),
            Matchers.equalTo("stdout logs")
        );
    }

    /**
     * RtLogs.stderr().fetch() fetches only stderr logs as String.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void logStderr() throws Exception {
        final Logs logs = new RtLogs(
            Mockito.mock(Container.class),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    this.prepareMessage("stderr logs")
                ),
                new Condition(
                    "Method should be a GET",
                    req -> req.getRequestLine().getMethod().equals("GET")
                ),
                new Condition(
                    "Resource path must be /123/logs",
                    req -> req.getRequestLine().getUri().endsWith(
                        "/123/logs?stdout=false&stderr=true"
                    )
                )
            ),
            URI.create("http://localhost:80/1.30/containers/123/logs")
        );
        MatcherAssert.assertThat(
            logs.stderr().fetch(),
            Matchers.equalTo("stderr logs")
        );
    }

}
