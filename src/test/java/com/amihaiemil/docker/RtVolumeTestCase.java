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
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for RtVolume.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 * @checkstyle MethodName (500 lines)
 */
public final class RtVolumeTestCase {

    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * RtVolume can return info about itself.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void inspectsItself() throws Exception {
        final Volume volume = new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createObjectBuilder()
                        .add("Name", "v1")
                        .add("Driver", "custom")
                        .add("Mountpoint", "/var/lib/docker/volumes/v1")
                        .add("Scope", "local")
                        .build().toString()
                ),
                new Condition(
                    "Method should be a GET",
                    req -> req.getRequestLine().getMethod().equals("GET")
                ),
                new Condition(
                    "Resource path must be /{name}",
                    req -> req.getRequestLine().getUri().endsWith("/v1")
                )
            ),
            URI.create("http://localhost:80/1.35/volumes/v1"),
            DOCKER
        );
        final JsonObject info = volume.inspect();
        MatcherAssert.assertThat(
            "Size of Json keys should be 4",
            info.keySet(),
            new IsCollectionWithSize<>(
                new IsEqual<>(4)
            )
        );
        MatcherAssert.assertThat(
            "Name value should be 'v1'",
            info.getString("Name"),
            new IsEqual<>("v1")
        );
        MatcherAssert.assertThat(
            "Driver value should be 'custom'",
            info.getString("Driver"),
            new IsEqual<>("custom")
        );
        MatcherAssert.assertThat(
            "Mountpoint value should be '/var/lib/docker/volumes/v1'",
            info.getString("Mountpoint"),
            new IsEqual<>("/var/lib/docker/volumes/v1")
        );
        MatcherAssert.assertThat(
            "Scope value should be 'local'",
            info.getString("Scope"),
            new IsEqual<>("local")
        );
    }

    /**
     * RtVolume.remove() must send a DELETE request to the volume's url.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void removeSendsCorrectRequest() throws Exception {
        new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    "RtVolume.remove() must send a DELETE HTTP request",
                    req -> "DELETE".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "RtVolume.remove() must send the request to the volume url",
                    req -> "http://localhost/volumes/test?force=false".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/volumes/test"),
            DOCKER
        ).remove();
    }

    /**
     * RtVolume.remove(true) must send a DELETE request to the volume's url
     * with force param set to true.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void removeWithForce() throws Exception {
        new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    "RtVolume.remove() must send a DELETE HTTP request",
                    req -> "DELETE".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "RtVolume.remove() must send the request to the volume url",
                    req -> "http://localhost/volumes/test?force=true".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/volumes/test"),
            DOCKER
        ).remove(true);
    }

    /**
     * RtVolume.remove() must throw UnexpectedResponseException if service
     * responds with 404.
     * @throws Exception The UnexpectedResponseException
     */
    @Test(expected = UnexpectedResponseException.class)
    public void removeErrorOn404() throws Exception {
        new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_NOT_FOUND)
            ),
            URI.create("http://localhost/volumes/test"),
            DOCKER
        ).remove();
    }

    /**
     * RtVolume.remove() must throw UnexpectedResponseException if service
     * responds with 409.
     * @throws Exception The UnexpectedResponseException
     */
    @Test(expected = UnexpectedResponseException.class)
    public void removeErrorOn409() throws Exception {
        new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_CONFLICT)
            ),
            URI.create("http://localhost/volumes/test"),
            DOCKER
        ).remove();
    }
}
