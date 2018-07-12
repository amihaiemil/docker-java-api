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

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import javax.json.Json;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link RtImages}.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle MethodName (500 lines)
 */
public final class RtImagesTestCase {
    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * Must return the same number of images as there are elements in the
     * json array returned by the service.
     */
    @Test
    public void iteratesImages() {
        final AtomicInteger count = new AtomicInteger();
        new RtImages(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createArrayBuilder()
                        .add(
                            Json.createObjectBuilder()
                                .add("Id", "sha256:e216a057b1cb1efc1")
                        ).add(
                            Json.createObjectBuilder()
                                .add("Id", "sha256:3e314f95dcace0f5e")
                        ).build().toString()
                )
            ),
            URI.create("http://localhost"),
            DOCKER
        ).forEach(image -> count.incrementAndGet());
        MatcherAssert.assertThat(
            count.get(),
            Matchers.is(2)
        );
    }

    /**
     * The iterator works when there are no containers.
     * @throws Exception If an error occurs.
     */
    @Test
    public void iteratesZeroImages() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        new RtImages(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createArrayBuilder().build().toString()
                )
            ),
            URI.create("http://localhost"),
            DOCKER
        ).forEach(image -> count.incrementAndGet());
        MatcherAssert.assertThat(
            count.get(),
            Matchers.is(0)
        );
    }
    
    /**
     * Must throw {@link UnexpectedResponseException} if response code is 500.
     * @throws Exception The UnexpectedException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void iterateFailsIfResponseIs500() throws Exception {
        new RtImages(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost"),
            DOCKER
        ).iterator();
    }

    /**
     * {@link RtImages#pull(String, String)} must construct the
     * URL with parameters correctly.
     * <p>
     * Notice the escaped characters for the 'fromSrc' parameter's value.
     * @throws Exception If an error occurs.
     */
    @Test
    public void createSetsGivenParameters() throws Exception {
        new RtImages(
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    // @checkstyle LineLength (1 line)
                    "RtImages.create() failed to correctly build the request URI.",
                    req -> {
                        System.out.println(req.getRequestLine().getUri());
                        return req.getRequestLine().getUri().endsWith(
                            // @checkstyle LineLength (1 line)
                            "/create?fromImage=testImage&tag=1.23"
                        );
                    }
                )
            ),
            URI.create("http://localhost"),
            DOCKER
        ).pull("testImage", "1.23");
    }

    /**
     * RtImages.create() must throw an {@link UnexpectedResponseException}
     * if the docker API responds with status code 404.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createErrorOnStatus404() throws Exception {
        new RtImages(
            new AssertRequest(
                new Response(HttpStatus.SC_NOT_FOUND)
            ),
            URI.create("http://localhost"),
            DOCKER
        ).pull("", "");
    }

    /**
     * RtImages.create() must throw an {@link UnexpectedResponseException}
     * if the docker API responds with status code 500.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createErrorOnStatus500() throws Exception {
        new RtImages(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost"),
            DOCKER
        ).pull("", "");
    }

    /**
     * RtImages.prune() sends correct request and exist successfully on
     * response code 200.
     * @throws Exception If an error occurs.
     */
    @Test
    public void prunesOk() throws Exception {
        new RtImages(
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    "prune() must send a POST request",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "prune() resource URL must be '/images/prune'",
                    req -> req.getRequestLine()
                        .getUri().endsWith("/images/prune")
                )
            ),
            URI.create("http://localhost/images"),
            DOCKER
        ).prune();
    }

    /**
     * RtImages.prune() must throw UnexpectedResponseException if service
     * responds with 500.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void pruneThrowsErrorOnResponse500() throws Exception {
        new RtImages(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost/images"),
            DOCKER
        ).prune();
    }
    
    /**
     * RtImages can return its Docker parent.
     */
    @Test
    public void returnsDocker() {
        MatcherAssert.assertThat(
            new RtImages(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_OK,
                        Json.createArrayBuilder().build().toString()
                    )
                ),
                URI.create("http://localhost"),
                DOCKER
            ).docker(),
            Matchers.is(DOCKER)
        );
    }
}
