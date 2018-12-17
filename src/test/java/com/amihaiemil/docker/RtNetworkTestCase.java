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
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link RtNetwork}.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 * @checkstyle MethodName (500 lines)
 * @todo #211:30min Finish implementing unit tests for RtNetwork methods
 *  connect, disconnect and add negative test cases for remove
 */
public final class RtNetworkTestCase {

    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * RtNetwork can return info about itself.
     * @throws Exception If something else goes wrong.
     */
    @Test
    @Ignore
    public void inspectsItself() throws Exception {
        final Network network = new RtNetwork(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createObjectBuilder()
                        .add("Name", "network1")
                        .add("Id", "id1")
                        .add("Driver", "bridge")
                        .add("Scope", "local")
                        .build().toString()
                ),
                new Condition(
                    "Method should be a GET",
                    req -> req.getRequestLine().getMethod().equals("GET")
                ),
                new Condition(
                    "Resource path must be /{id}",
                    req -> req.getRequestLine().getUri().endsWith("/id1")
                )
            ),
            URI.create("http://localhost/network/id1"),
            DOCKER
        );
        final JsonObject info = network.inspect();
        MatcherAssert.assertThat(
            "Name value should be 'network1'",
            info.getString("Name"),
            new IsEqual<>("network1")
        );
        MatcherAssert.assertThat(
            "Id value should be 'id1'",
            info.getString("Id"),
            new IsEqual<>("id1")
        );
        MatcherAssert.assertThat(
            "Driver value should be 'bridge'",
            info.getString("Driver"),
            new IsEqual<>("bridge")
        );
        MatcherAssert.assertThat(
            "Scope value should be 'local'",
            info.getString("Scope"),
            new IsEqual<>("local")
        );
    }

    /**
     * RtNetwork.remove() must send a DELETE request to the network's url.
     * @throws Exception If something goes wrong.
     */
    @Test
    @Ignore
    public void removeSendsCorrectRequest() throws Exception {
        new RtNetwork(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    "remove() must send a DELETE HTTP request",
                    req -> "DELETE".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "remove() must send the request to the network url",
                    req -> "http://localhost/network/id1".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/network/id1"),
            DOCKER
        ).remove();
    }
}
