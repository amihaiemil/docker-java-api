/**
 * Copyright (c) 2018, Mihai Emil Andronache
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

import com.jcabi.http.Response;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Unit tests for SocketResponse.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SocketResponseTestCase {

    /**
     * A SocketResponse can return its status.
     */
    @Test
    public void hasStatus() {
        final String responseString = "HTTP/1.1 200 OK\n"
            + "Api-Version: 1.35\n"
            + "Content-Type: application/json\n"
            + "Docker-Experimental: false\n"
            + "Ostype: linux\n"
            + "Server: Docker/17.12.0-ce (linux)\n"
            + "Date: Sun, 04 Feb 2018 09:07:13 GMT\n"
            + "Connection: close\n"
            + "Content-Length: 0";
        final Response resp = new SocketResponse(null, responseString);
        MatcherAssert.assertThat(resp.status(), Matchers.is(200));
    }

    /**
     * A SocketResponse can return its status phrase.
     */
    @Test
    public void hasReason() {
        final String responseString = "HTTP/1.1 200 OK\n"
            + "Api-Version: 1.35\n"
            + "Content-Type: application/json\n"
            + "Docker-Experimental: false\n"
            + "Ostype: linux\n"
            + "Server: Docker/17.12.0-ce (linux)\n"
            + "Date: Sun, 04 Feb 2018 09:07:13 GMT\n"
            + "Connection: close\n"
            + "Content-Length: 0";
        final Response resp = new SocketResponse(null, responseString);
        MatcherAssert.assertThat(resp.reason(), Matchers.equalTo("OK"));
    }

    /**
     * A SocketResponse can return its headers if there is no body afterwards.
     */
    @Test
    public void headersWithNoContent() {
        final String responseString = "HTTP/1.1 200 OK\n"
            + "Api-Version: 1.35\n"
            + "Content-Type: application/json\n"
            + "Docker-Experimental: false\n"
            + "Ostype: linux\n"
            + "Server: Docker/17.12.0-ce (linux)\n"
            + "Date: Sun, 04 Feb 2018 09:07:13 GMT\n"
            + "Connection: close\n"
            + "Content-Length: 0";
        final Response resp = new SocketResponse(null, responseString);
        final Map<String, List<String>> headers = resp.headers();
        MatcherAssert.assertThat(headers.size(), Matchers.is(8));
        headers.forEach((key, value) -> {
            MatcherAssert.assertThat(
                key, Matchers.not(Matchers.isEmptyOrNullString())
            );
            MatcherAssert.assertThat(
                value.size() > 0, Matchers.is(Boolean.TRUE)
            );
        });
    }

    /**
     * A SocketResponse can return its headers if there is a body afterwards.
     */
    @Test
    public void headersWithontent() {
        final String responseString = "HTTP/1.1 200 OK\n"
                + "Api-Version: 1.35\n"
                + "Content-Type: application/json\n"
                + "Docker-Experimental: false\n"
                + "Connection: close\n"
                + "Content-Length: 2\n\n"
                + "OK";
        final Response resp = new SocketResponse(null, responseString);
        final Map<String, List<String>> headers = resp.headers();
        MatcherAssert.assertThat(headers.size(), Matchers.is(5));
        headers.forEach((key, value) -> {
            MatcherAssert.assertThat(
                key, Matchers.not(Matchers.isEmptyOrNullString())
            );
            MatcherAssert.assertThat(
                value.size() == 1, Matchers.is(Boolean.TRUE)
            );
        });
        MatcherAssert.assertThat(headers.get(
            "Api-Version").get(0), Matchers.equalTo("1.35")
        );
        MatcherAssert.assertThat(headers.get(
            "Content-Type").get(0), Matchers.equalTo("application/json")
        );
        MatcherAssert.assertThat(headers.get(
            "Docker-Experimental").get(0), Matchers.equalTo("false")
        );
        MatcherAssert.assertThat(headers.get(
            "Connection").get(0), Matchers.equalTo("close")
        );
        MatcherAssert.assertThat(headers.get(
            "Content-Length").get(0), Matchers.equalTo("2")
        );
    }

    /**
     * A SocketResponse returns an empty Map when there are no headers.
     */
    @Test
    public void noHeaders() {
        final String responseString = "HTTP/1.1 200 OK\n";
        final Response resp = new SocketResponse(null, responseString);
        final Map<String, List<String>> headers = resp.headers();
        MatcherAssert.assertThat(headers.isEmpty(), Matchers.is(Boolean.TRUE));
    }
}
