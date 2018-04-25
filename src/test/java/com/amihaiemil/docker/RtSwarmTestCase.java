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
import java.net.URI;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for {@link RtSwarm}.
 * @author George Aristy (george.aristy@gmail.com)
 * @see <a href="https://docs.docker.com/engine/api/v1.35/#tag/Swarm">Docker Swarm API</a>
 * @version $Id$
 * @since 0.0.1
 * @checkstyle MethodName (500 lines)
 */
public final class RtSwarmTestCase {

    /**
     * Leave request works with force.
     * @throws Exception If an error occurs.
     */
    @Test
    public void forcedLeave() throws Exception {
        new RtSwarm(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "{}"
                ),
                new Condition(
                    "Request method should be GET",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "URI is not ok!",
                    req -> "http://localhost/swarm/leave?force=true".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/swarm")
        ).leave(true);
    }

    /**
     * Leave request works without force.
     * @throws Exception If an error occurs.
     */
    @Test
    public void leavesWithoutForce() throws Exception {
        new RtSwarm(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "{}"
                ),
                new Condition(
                    "Request method should be GET",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "URI is not ok!",
                    req -> "http://localhost/swarm/leave?force=false".equals(
                            req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/swarm")
        ).leave(false);
    }

    /**
     * Leave request receives 500 SERVER ERROR response.
     * @throws Exception If an error occurs.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void leavesWithServerError() throws Exception {
        new RtSwarm(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Internal Error"
                )
            ),
            URI.create("http://localhost/swarm")
        ).leave(false);
    }

    /**
     * Leave request receives 503 SERVICE UNAVAILABLE response.
     * @throws Exception If an error occurs.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void leavesWithServiceUnavailable() throws Exception {
        new RtSwarm(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_SERVICE_UNAVAILABLE,
                    "Internal Error"
                )
            ),
            URI.create("http://localhost/swarm")
        ).leave(false);
    }

    /**
     * Inspect request must be well-formed.
     * @throws Exception If an error occurs.
     */
    @Test
    public void inspectRequestIsWellformed() throws Exception {
        new RtSwarm(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "{ \"ID\": \"abajmipo7b4xz5ip2nrla6b11\" }"
                ),
                new Condition(
                    "Request method should be GET",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "URI must be equal to the one passed in ctor",
                    req -> "http://localhost".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost")
        ).inspect();
    }

    /**
     * Inspect should return JSON if docker service response is OK.
     * @throws Exception If an error occurs.
     */
    @Test
    public void inspectReturnsJsonIfResponseIsOk() throws Exception {
        MatcherAssert.assertThat(
            new RtSwarm(
              new AssertRequest(
                  new Response(
                      HttpStatus.SC_OK,
                    "{ \"ID\": \"abajmipo7b4xz5ip2nrla6b11\" }"
                  )
              ),
              URI.create("http://localhost")
            ).inspect(),
            Matchers.notNullValue()
        );
    }

    /**
     * Inspect throws {@link UnexpectedResponseException} if docker response
     * is 404.
     * @throws Exception If an error occurs.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void inspectThrowsErrorIfResponseIs404() throws Exception {
        new RtSwarm(
            new AssertRequest(
                new Response(HttpStatus.SC_NOT_FOUND, "")
            ), URI.create("http://localhost")
        ).inspect();
    }

    /**
     * Inspect throws {@link UnexpectedResponseException} if docker response
     * is 500.
     * @throws Exception If an error occurs.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void inspectThrowsErrorIfResponseIs500() throws Exception {
        new RtSwarm(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR, "")
            ), URI.create("http://localhost")
        ).inspect();
    }

    /**
     * Inspect throws {@link UnexpectedResponseException} if docker response
     * is 503.
     * @throws Exception If an error occurs.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void inspectThrowsErrorIfResponseIs503() throws Exception {
        new RtSwarm(
            new AssertRequest(
                new Response(HttpStatus.SC_SERVICE_UNAVAILABLE, "")
            ), URI.create("http://localhost")
        ).inspect();
    }

    /**
     * The init request should have correct URI, method, and also its JSON
     * payload should have at least the 'ListenAddr' and 'ForceNewCluster'
     * properties.
     * @throws Exception If an error occurs.
     */
    @Test
    public void initRequestWellformed() throws Exception {
        final String listenAddress = "172.27.9.10";
        new RtSwarm(
            new AssertRequest(
                new Response(HttpStatus.SC_OK, "sometoken123"),
                new Condition(
                    "Request method must be POST.",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "The 'ListenAddr' attribute is mandatory.",
                    req -> new PayloadOf(req).containsKey("ListenAddr")
                ),
                new Condition(
                    "The 'ListenAddr' value must be the same as the one given.",
                    req -> listenAddress.equals(
                        new PayloadOf(req).getString("ListenAddr")
                    )
                ),
                new Condition(
                    "The 'ForceNewCluster' attribute is mandatory.",
                    req -> new PayloadOf(req).containsKey("ForceNewCluster")
                ),
                new Condition(
                    "The 'ForceNewCluster' attribute cannot be empty.",
                    req -> !new PayloadOf(req).isNull("ForceNewCluster")

                )
            ),
            URI.create("http://docker/swarm")
        ).init(listenAddress);
    }

    /**
     * Must return the same token returned by Docker.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void initReturnsSwarmToken() throws Exception {
        MatcherAssert.assertThat(
            new RtSwarm(
                new AssertRequest(
                    new Response(HttpStatus.SC_OK, "sometoken123")
                ),
                URI.create("http://docker")
            ).init("123"),
            Matchers.is("sometoken123")
        );
    }

    /**
     * Must throw {@link UnexpectedResponseException} if docker responds with
     * a code different from 200.
     * @throws Exception If an error occurs.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void initUnexpectedErrorIfResponseIsNot200() throws Exception {
        new RtSwarm(
            new AssertRequest(
                new Response(HttpStatus.SC_BAD_REQUEST, "")
            ),
            URI.create("http://docker")
        ).init("123");
    }
}
