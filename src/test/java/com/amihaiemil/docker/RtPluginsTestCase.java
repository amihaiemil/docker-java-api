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

import com.amihaiemil.docker.mock.ArrayPayloadOf;
import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link RtPlugins}.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 * @checkstyle MethodName (500 lines)
 */
public final class RtPluginsTestCase {

    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * RtPlugins.create(name, directory) must send
     * a correct POST request sends and exist successfully on response code 204.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createOk() throws Exception {
        new ListedPlugins(
            new AssertRequest(
                new Response(HttpStatus.SC_NO_CONTENT),
                new Condition(
                    "create() must send a POST HTTP request",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "create() must send the request to the create url",
                    req -> "http://localhost/plugins/create?name=plugin".equals(
                        req.getRequestLine().getUri()
                    )
                ),
                new Condition(
                    "create() must send String body request",
                    req -> "/home/pluginDir".equals(this.stringPayloadOf(req))
                )
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).create("plugin", "/home/pluginDir");
    }

    /**
     * RtPlugins.create(name, directory) must
     * throw UnexpectedResponseException if service responds with 500.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createThrowsErrorOnResponse500() throws Exception {
        new ListedPlugins(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).create("plugin", "/home/pluginDir");
    }

    /**
     * RtPlugins.pullAndInstall(remote, name, properties) must send
     * a correct POST request sends and exist successfully on response code 204.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void pullAndInstallOk() throws Exception {
        final JsonArray properties = Json.createArrayBuilder()
            .add(
                Json.createObjectBuilder()
                    .add("Name", "network")
                    .add("Description", "")
                    .add("Value", "host")
            ).build();
        new ListedPlugins(
            new AssertRequest(
                new Response(HttpStatus.SC_NO_CONTENT),
                new Condition(
                    "pullAndInstall() must send a POST HTTP request",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "pullAndInstall() must send the request to the pull url",
                    req -> {
                        String correctUrl =
                            String.join("", "http://localhost/plugins/pull?",
                                "remote=vieus%2Fsshfs&name=sshfs");
                        return correctUrl.equals(req.getRequestLine().getUri());
                    }
                ),
                new Condition(
                    "pullAndInstall() must send Json body request",
                    req -> {
                        JsonObject payload =
                            new ArrayPayloadOf(req).next();
                        return "network".equals(payload.getString("Name"))
                            && "host".equals(payload.getString("Value"));
                    }
                )
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).pullAndInstall("vieus/sshfs", "sshfs", properties);
    }

    /**
     * RtPlugins.pullAndInstall(remote, name, properties)
     * must throw UnexpectedResponseException if service responds with 500.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void pullAndInstallThrowsErrorOnResponse500() throws Exception {
        new ListedPlugins(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).pullAndInstall("vieus/sshfs", "sshfs",
            Json.createArrayBuilder().build()
        );
    }

    /**
     * RtPlugins.privileges(remote) must a correct GET request and retrieve
     * list of plugin privileges.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test
    public void iteratePluginPrivileges() throws Exception {
        final Iterator<PluginPrivilege> privileges = new ListedPlugins(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                            .add("Name", "p1")
                            .add("Description", "p1 desc")
                            .add("Value",
                                Json.createArrayBuilder()
                                    .add("/data")
                            )
                        ).build().toString()
                )
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).privileges("test");
        final PluginPrivilege privilege = privileges.next();
        MatcherAssert.assertThat(
            "Name must be 'p1'",
            privilege.name(),
            new IsEqual<>("p1")
        );
        MatcherAssert.assertThat(
            "Description must be 'p1 desc'",
            privilege.description(),
            new IsEqual<>("p1 desc")
        );
        MatcherAssert.assertThat(
            "First value must be '/data'",
            privilege.value().iterator().next(),
            new IsEqual<>("/data")
        );
    }

    /**
     * Extracts request payload as String.
     * @param request Http Request.
     * @return Payload as String.
     */
    private String stringPayloadOf(final HttpRequest request) {
        try {
            final String payload;
            if (request instanceof HttpEntityEnclosingRequest) {
                payload = EntityUtils.toString(
                    ((HttpEntityEnclosingRequest) request).getEntity()
                );
            } else {
                payload = "";
            }
            return payload;
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "Cannot read request payload", ex
            );
        }
    }

}
