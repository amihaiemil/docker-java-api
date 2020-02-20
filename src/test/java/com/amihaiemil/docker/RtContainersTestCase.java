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
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for RtContainers.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @see <a href="https://docs.docker.com/engine/api/v1.30/#operation/ContainerCreate">Docker ContainerCreate API</a>
 * @version $Id$
 * @since 0.0.1
 * @checkstyle MethodName (500 lines)
 */
public final class RtContainersTestCase {
    
    /**
     * RtContainers can return its parent Docker.
     */
    @Test
    public void returnsDocker() {
        final Docker parent = Mockito.mock(Docker.class);
        MatcherAssert.assertThat(
            new ListedContainers(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_OK,
                        Json.createArrayBuilder().build().toString()
                    )
                ),
                URI.create("http://localhost"),
                parent
            ).docker(),
            Matchers.is(parent)
        );
    }
    
    /**
     * Must return the same number of containers as there are elements in the
     * json array returned by the service.
     * @throws Exception If an error occurs.
     */
    @Test
    public void returnsAllContainers() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        new ListedContainers(
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
                ),
                new Condition(
                    "Resource path must be /json",
                    req -> req.getRequestLine().getUri().endsWith(
                        "/json?all=true"
                    )
                )
            ),
            URI.create("http://localhost/containers/"),
            Mockito.mock(Docker.class)
        ).all().forEachRemaining(container -> count.incrementAndGet());
        MatcherAssert.assertThat(
            count.get(),
            Matchers.is(2)
        );
    }
    
    /**
     * Must return the same number of containers as there are elements in the
     * json array returned by the service.
     * @throws Exception If an error occurs.
     */
    @Test
    public void iteratesRunningContainers() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        new ListedContainers(
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
                ),
                new Condition(
                    "Resource path must be /json",
                    req -> req.getRequestLine().getUri().endsWith("/json")
                )
            ),
            URI.create("http://localhost/containers/json"),
            Mockito.mock(Docker.class)
        ).forEach(container -> count.incrementAndGet());
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
    public void iteratesZeroContainers() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createArrayBuilder().build().toString()
                )
            ), URI.create("http://localhost"), Mockito.mock(Docker.class)
        ).forEach(container -> count.incrementAndGet());
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
        new ListedContainers(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost"),
            Mockito.mock(Docker.class)
        ).iterator();
    }
    
    /**
     * Must throw {@link UnexpectedResponseException} if response code is 400.
     * @throws Exception The UnexpectedException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void iterateFailsIfResponseIs400() throws Exception {
        new ListedContainers(
            new AssertRequest(
                new Response(HttpStatus.SC_BAD_REQUEST)
            ),
            URI.create("http://localhost"),
            Mockito.mock(Docker.class)
        ).iterator();
    }
    
    /**
     * The request should be well-formed.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsContainerOk()
        throws Exception {
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED,
                    "{ \"Id\": \"df2419f4\", \"Warnings\": [ ]}"
                ),
                new Condition(
                    "The 'Content-Type' header must be set.",
                    req -> req.getHeaders("Content-Type").length > 0
                ),
                new Condition(
                    "Content-Type must be 'application/json'.",
                    // @checkstyle LineLength (1 line)
                    req -> "application/json".equals(req.getHeaders("Content-Type")[0].getValue())
                ),
                new Condition(
                    "Resource path must be /create",
                    // @checkstyle LineLength (1 line)
                    req -> req.getRequestLine().getUri().endsWith("/create")
                ),
                new Condition(
                    "The 'Image' attribute must be set in the payload.",
                    // @checkstyle LineLength (1 line)
                    req -> "some_image".equals(new PayloadOf(req).getString("Image"))
                )
            ), URI.create("http://localhost/test"), Mockito.mock(Docker.class)
        ).create("some_image");
    }

    /**
     * Returns a container if the service call is successful.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void returnsCreatedContainer() throws Exception {
        MatcherAssert.assertThat(
            new ListedContainers(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_CREATED,
                        "{ \"Id\": \"df2419f4\", \"Warnings\": [ ]}"
                    )
                ),
                URI.create("http://localhost/test"),
                Mockito.mock(Docker.class)
            ).create("some_image"),
            Matchers.notNullValue()
        );
    }

    /**
     * Must fail if docker responds with error code 400.
     * @throws IOException due to code 400
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createsWith400() throws IOException {
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_BAD_REQUEST
                )
            ),
            URI.create("http://localhost/test"),
            Mockito.mock(Docker.class)
        ).create("some_image");
    }

    /**
     * Must fail if docker responds with error code 404.
     * @throws IOException due to code 404
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createsWith404() throws IOException {
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NOT_FOUND
                )
            ),
            URI.create("http://localhost/test"),
            Mockito.mock(Docker.class)    
        ).create("some_image");
    }

    /**
     * Must fail if docker responds with error code 406.
     * @throws IOException due to code 406
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createsWith406() throws IOException {
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NOT_ACCEPTABLE
                )
            ),
            URI.create("http://localhost/test"),
            Mockito.mock(Docker.class)
        ).create("some_image");
    }

    /**
     * Must fail if docker responds with error code 409.
     * @throws IOException due to code 409
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createsWithConflict() throws IOException {
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CONFLICT
                )
            ),
            URI.create("http://localhost/test"),
            Mockito.mock(Docker.class)
        ).create("some_image");
    }

    /**
     * Must fail if docker responds with error code 500.
     * @throws IOException due to code 500
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createsWithServerErrpr() throws IOException {
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_INTERNAL_SERVER_ERROR
                )
            ),
            URI.create("http://localhost/test"),
            Mockito.mock(Docker.class)
        ).create("some_image");
    }

    /**
     * Test for
     * {@link RtContainers#create(String, String)}: The request URI should be
     * well-formed and include the 'name' query param.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsWithImageName() throws Exception {
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED,
                    "{ \"Id\": \"df2419f4\", \"Warnings\": [ ]}"
                ),
                new Condition(
                    "Resource path must be /create?name=some_name",
                    // @checkstyle LineLength (1 line)
                    req -> req.getRequestLine().getUri().endsWith("/create?name=some_name")
                )
            ),
            URI.create("http://localhost/test"),
            Mockito.mock(Docker.class)    
        ).create("some_name", "some_image");
    }

    /**
     * Test for {@link RtContainers#create(JsonObject)}: request URI must be
     * well-formed and payload must include the input JSON.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsWithPayloadForCreateJson() throws Exception {
        final JsonObject json = Json.createObjectBuilder()
            .add("Image", "ubuntu")
            .add("Entrypoint", "script.sh")
            .add("StopSignal", "SIGTERM")
            .build();
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED,
                    "{ \"Id\": \"df2419f4\", \"Warnings\": [ ]}"
                ),
                new Condition(
                    "Resource path must be /create",
                    req -> req.getRequestLine().getUri().endsWith("/create")
                ),
                new Condition(
                    "Payload must include the input JSON attributes.",
                    req -> {
                        final JsonObject payload = new PayloadOf(req);
                        // @checkstyle LineLength (3 lines)
                        return payload.getString("Image").equals(json.getString("Image"))
                            && payload.getString("Entrypoint").equals(json.getString("Entrypoint"))
                            && payload.getString("StopSignal").equals(json.getString("StopSignal"));
                    }
                )
            ),
            URI.create("http://localhost/test"),
            Mockito.mock(Docker.class)    
        ).create(json);
    }

    /**
     * Test for {@link RtContainers#create(String, JsonObject)}: request URI
     * must be  well-formed and payload must include the input JSON.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsWithPayloadForCreateNameAndJson() throws Exception {
        final JsonObject json = Json.createObjectBuilder()
            .add("Image", "ubuntu")
            .add("Entrypoint", "script.sh")
            .add("StopSignal", "SIGTERM")
            .build();
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED,
                    "{ \"Id\": \"df2419f4\", \"Warnings\": [ ]}"
                ),
                new Condition(
                    "Resource path must be /create?name=image_name",
                    // @checkstyle LineLength (1 line)
                    req -> req.getRequestLine().getUri().endsWith("/create?name=image_name")
                ),
                new Condition(
                    "Payload must include the input JSON attributes.",
                    req -> {
                        final JsonObject payload = new PayloadOf(req);
                        // @checkstyle LineLength (3 lines)
                        return payload.getString("Image").equals(json.getString("Image"))
                            && payload.getString("Entrypoint").equals(json.getString("Entrypoint"))
                            && payload.getString("StopSignal").equals(json.getString("StopSignal"));
                    }
                )
            ),
            URI.create("http://localhost/test"),
            Mockito.mock(Docker.class)
        ).create("image_name", json);
    }

    /**
     * Bug #109: RtContainers.create() must encode URL parameters.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createEscapesNameParameter() throws Exception {
        new ListedContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED,
                    "{ \"Id\": \"df2419f4\", \"Warnings\": [ ]}"
                ),
                new Condition(
                    "RtContainers.create() must encode URL parameter",
                    req -> req.getRequestLine()
                        .getUri().endsWith("name=Adrian+Toomes")
                )
            ),
            URI.create("http://localhost/docker"),
            Mockito.mock(Docker.class)    
        ).create("Adrian Toomes", "some/image");
    }

    /**
     * RtContainers.create() returns an RtContainer with the given parameter.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsContainerWithGivenParameters() throws Exception {
        MatcherAssert.assertThat(
            new ListedContainers(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_CREATED,
                        "{ \"Id\": \"df2419f4\", \"Warnings\": [ ]}"
                    )
                ),
                URI.create("http://localhost/test"),
                Mockito.mock(Docker.class)
            ).create(
                Json.createObjectBuilder()
                    .add("Image", "ubuntu").build()
            ).getString("Image"),
            Matchers.is("ubuntu")
        );
    }

    /**
     * RtContainers.create() returns an RtContainer with the docker id.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsContainerWithId() throws Exception {
        MatcherAssert.assertThat(
            new ListedContainers(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_CREATED,
                        "{ \"Id\": \"df2419f4\", \"Warnings\": [ ] }"
                    )
                ),
                URI.create("http://localhost/test"),
                Mockito.mock(Docker.class)    
            ).create(
                Json.createObjectBuilder()
                    .add("Image", "ubuntu").build()
            ).getString("Id"),
            Matchers.is("df2419f4")
        );
    }
}
