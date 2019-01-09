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
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link RtNetworks}.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @version $Id$
 * @since 0.0.4
 * @checkstyle MethodName (500 lines)
 */
public final class RtNetworksTestCase {

    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * RtNetworks.prune() sends correct request and exist successfully on
     * response code 200.
     * @throws Exception If an error occurs.
     */
    @Test
    public void prunesOk() throws Exception {
        new ListedNetworks(
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    "prune() must send a POST request",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "prune() resource URL must be '/networks/prune'",
                    req -> req.getRequestLine()
                        .getUri().endsWith("/networks/prune")
                )
            ),
            URI.create("http://localhost/networks"),
            DOCKER
        ).prune();
    }

    /**
     * RtNetworks.prune() must throw UnexpectedResponseException if service
     * responds with 500.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void pruneThrowsErrorOnResponse500() throws Exception {
        new ListedNetworks(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost/networks"),
            DOCKER
        ).prune();
    }

    /**
     * RtNetworks.create() must send a correct POST request sends
     * and exist successfully on response code 201.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createOk() throws Exception {
        final Network network = new ListedNetworks(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED,
                    Json.createObjectBuilder()
                        .add("Id", "id1").build().toString()
                ),
                new Condition(
                    "create() must send a POST HTTP request",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "create() must send the request to the create url",
                    req -> "http://localhost/networks/create".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/networks"),
            DOCKER
        ).create("test");
        MatcherAssert.assertThat(
            network.getString("Id"),
            new IsEqual<>("id1")
        );
    }

    /**
     * RtNetworks.create() must send a correct POST request sends
     * and exist successfully on response code 201.
     * @throws Exception If something goes wrong.
     * @checkstyle ExecutableStatementCount (100 lines)
     */
    @Test
    public void createWithParametersOk() throws Exception {
        final JsonObject ipam = Json.createObjectBuilder()
            .add("Driver", "IPAMDriver")
            .add("Subnet", "200.255.255.255/24")
            .add("Gateway", "200.15.110.245")
            .add("AuxAddress", "auxiliar:200.15.110.200")
            .add("driveroption1", "some driver option")
            .add("driveroption1", "another driver option").build();
        final JsonObject options = Json.createObjectBuilder()
            .add("option1", "option one")
            .add("option2", "option two").build();
        final JsonObject labels = Json.createObjectBuilder()
            .add("label1", "label one")
            .add("label2", "label two").build();
        final Network network =
            new ListedNetworks(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_CREATED,
                        Json.createObjectBuilder()
                            .add("Id", "id1").build().toString()
                    ),
                    new Condition(
                        "create() must send a POST HTTP request",
                        req -> "POST".equals(req.getRequestLine().getMethod())
                    ),
                    new Condition(
                        "create() must send the request to the create url",
                        req -> "http://localhost/networks/create".equals(
                            req.getRequestLine().getUri()
                        )
                    ),
                    new Condition(
                        "create() must send Json body request",
                        req -> {
                            final JsonObject payload = new PayloadOf(req);
                            // @checkstyle LineLength (3 lines)
                            return payload.getJsonObject("IPAM").getString("Driver").equals(ipam.getString("Driver"))
                                && payload.getJsonObject("Options").getString("option1").equals(options.getString("option1"))
                                && payload.getJsonObject("Labels").getString("label1").equals(labels.getString("label1"));
                        }
                    )
                ),
                URI.create("http://localhost/networks"),
                DOCKER
            ).create(
                "testwithparameters",
                Json.createObjectBuilder()
                .add("Driver", "networkdriver")
                .add(
                    "IPAM",
                    ipam
                ).add(
                    "Options",
                    options
                ).add(
                    "Labels",
                    labels
                ).build()
            );
        MatcherAssert.assertThat(
            "could not return correct network id",
            network.getString("Id"),
            new IsEqual<>("id1")
        );
    }

    /**
     * RtNetworks.create() must throw IOException if response is empty.
     * @throws Exception The IOException.
     */
    @Test(expected = IOException.class)
    public void createThrowsErrorOnEmptyResponse() throws Exception {
        new ListedNetworks(
            new AssertRequest(
                new Response(HttpStatus.SC_CREATED)
            ),
            URI.create("http://localhost/networks"),
            DOCKER
        ).create("test");
    }

    /**
     * RtNetworks.create() must throw UnexpectedResponseException if service
     * responds with 500.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createThrowsErrorOnResponse500() throws Exception {
        new ListedNetworks(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost/networks"),
            DOCKER
        ).create("test");
    }

    /**
     * RtNetworks can return its Docker parent.
     */
    @Test
    public void returnsDocker() {
        MatcherAssert.assertThat(
            new ListedNetworks(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_OK,
                        Json.createArrayBuilder().build().toString()
                    )
                ),
                URI.create("http://localhost"),
                DOCKER
            ).docker(),
            new IsEqual<>(DOCKER)
        );
    }
}
