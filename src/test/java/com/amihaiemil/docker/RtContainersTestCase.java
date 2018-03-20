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
import com.amihaiemil.docker.mock.PayloadOf;
import com.amihaiemil.docker.mock.Response;
import java.io.IOException;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit tests for RtContainers.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @see <a href="https://docs.docker.com/engine/api/v1.30/#operation/ContainerCreate">Docker ContainerCreate API</a>
 * @version $Id$
 * @since 0.0.1
 * @checkstyle MethodName (500 lines)
 * @todo #47:30min Impediment: once #45 is done, unignore the following tests
 *  and refactor accordingly if needed: ioErrorIfResponseIs400,
 *  ioErrorIfResponseIs404, ioErrorIfResponseIs406, ioErrorIfResponseIs409,
 *  ioErrorIfResponseIs500.
 */
public final class RtContainersTestCase {
    /**
     * The request should be well-formed.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void wellformedRequestForCreateContainerFromImage()
        throws Exception {
        new RtContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED, "{ \"Id\": \"df2419f4\" }"
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
            ), URI.create("http://localhost/test")
        ).create("some_image");
    }

    /**
     * Returns a container if the service call is successful.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void returnsContainerIfCallIsSuccessful() throws Exception {
        MatcherAssert.assertThat(
            new RtContainers(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_CREATED,
                        "{ \"Id\": \"df2419f4\" }"
                    )
                ), URI.create("http://localhost/test")
            ).create("some_image"),
            Matchers.notNullValue()
        );
    }

    /**
     * Must fail if docker responds with error code 400.
     * @throws IOException due to code 400
     */
    @Ignore
    @Test(expected = IOException.class)
    public void ioErrorIfResponseIs400() throws IOException {
        new RtContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_BAD_REQUEST,
                    ""
                )
            ), URI.create("http://localhost/test")
        ).create("some_image");
    }

    /**
     * Must fail if docker responds with error code 404.
     * @throws IOException due to code 404
     */
    @Ignore
    @Test(expected = IOException.class)
    public void ioErrorIfResponseIs404() throws IOException {
        new RtContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NOT_FOUND,
                    ""
                )
            ), URI.create("http://localhost/test")
        ).create("some_image");
    }

    /**
     * Must fail if docker responds with error code 406.
     * @throws IOException due to code 406
     */
    @Ignore
    @Test(expected = IOException.class)
    public void ioErrorIfResponseIs406() throws IOException {
        new RtContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NOT_ACCEPTABLE,
                    ""
                )
            ), URI.create("http://localhost/test")
        ).create("some_image");
    }

    /**
     * Must fail if docker responds with error code 409.
     * @throws IOException due to code 409
     */
    @Ignore
    @Test(expected = IOException.class)
    public void ioErrorIfResponseIs409() throws IOException {
        new RtContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CONFLICT,
                    ""
                )
            ), URI.create("http://localhost/test")
        ).create("some_image");
    }

    /**
     * Must fail if docker responds with error code 500.
     * @throws IOException due to code 500
     */
    @Ignore
    @Test(expected = IOException.class)
    public void ioErrorIfResponseIs500() throws IOException {
        new RtContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    ""
                )
            ), URI.create("http://localhost/test")
        ).create("some_image");
    }

    /**
     * Test for
     * {@link RtContainers#create(String, String)}: The request URI should be
     * well-formed and include the 'name' query param.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void wellformedUriForCreateNameImage() throws Exception {
        new RtContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED, "{ \"Id\": \"df2419f4\" }"
                ),
                new Condition(
                    "Resource path must be /create?name=some_name",
                    // @checkstyle LineLength (1 line)
                    req -> req.getRequestLine().getUri().endsWith("/create?name=some_name")
                )
            ), URI.create("http://localhost/test")
        ).create("some_name", "some_image");
    }

    /**
     * Test for {@link RtContainers#create(JsonObject)}: request URI must be
     * well-formed and payload must include the input JSON.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void wellformedUriAndPayloadForCreateJson() throws Exception {
        final JsonObject json = Json.createObjectBuilder()
            .add("Image", "ubuntu")
            .add("Entrypoint", "script.sh")
            .add("StopSignal", "SIGTERM")
            .build();
        new RtContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED, "{ \"Id\": \"df2419f4\" }"
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
            ), URI.create("http://localhost/test")
        ).create(json);
    }

    /**
     * Test for {@link RtContainers#create(String, JsonObject)}: request URI
     * must be  well-formed and payload must include the input JSON.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void wellformedUriAndPayloadForCreateNameAndJson() throws Exception {
        final JsonObject json = Json.createObjectBuilder()
            .add("Image", "ubuntu")
            .add("Entrypoint", "script.sh")
            .add("StopSignal", "SIGTERM")
            .build();
        new RtContainers(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED, "{ \"Id\": \"df2419f4\" }"
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
            ), URI.create("http://localhost/test")
        ).create("image_name", json);
    }
}
