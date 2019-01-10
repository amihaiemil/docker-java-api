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
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link RtPlugin}.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.8
 * @todo #266:30min Extract method stringPayloadOf to class to reuse in
 *  other test cases (like in RtPluginsTestCase.createOk() method)
 * @checkstyle MethodName (500 lines)
 */
public final class RtPluginTestCase {

    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * RtPlugin can return info about itself.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void inspectsItself() throws Exception {
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createObjectBuilder()
                        .add("Name", "p1")
                        .add("Driver", "custom")
                        .add("Enabled", true)
                        .build().toString()
                ),
                new Condition(
                    "Method should be a GET",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/json",
                    req -> req.getRequestLine().getUri().endsWith("/p1/json")
                )
            ),
            URI.create("http://localhost:80/1.35/plugins/p1"),
            DOCKER
        );
        final JsonObject info = plugin.inspect();
        MatcherAssert.assertThat(
            "Size of Json keys should be 3",
            info.keySet(),
            new IsCollectionWithSize<>(
                new IsEqual<>(3)
            )
        );
        MatcherAssert.assertThat(
            "Name value should be 'p1'",
            info.getString("Name"),
            new IsEqual<>("p1")
        );
        MatcherAssert.assertThat(
            "Driver value should be 'custom'",
            info.getString("Driver"),
            new IsEqual<>("custom")
        );
        MatcherAssert.assertThat(
            "Must be enabled",
            info.getBoolean("Enabled"),
            new IsEqual<>(true)
        );
    }

    /**
     * RtPlugin enable itself.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void enablesItself() throws Exception {
        new ListedPlugins(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NO_CONTENT
                )
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).pullAndInstall(
            "vieus/sshfs",
            "sshfs",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                .add("Name", "network")
                .add("Description", "")
                .add("Value", "host")
            ).build()
        );
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/enable",
                    req -> req.getRequestLine().getUri()
                    .endsWith("/sshfs/enable")
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        plugin.enable();
    }

    /**
     * RtPlugin enable() must throw UnexpectedResponseException
     * if service responds with 404.
     * @throws Exception If something goes wrong.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void failsToEnableItselfWhenNotInstalled() throws Exception {
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NOT_FOUND
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/enable",
                    req -> req.getRequestLine().getUri()
                    .endsWith("/sshfs/enable")
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        plugin.enable();
    }

    /**
     * RtPlugin disables itself.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void disablesItself() throws Exception {
        new ListedPlugins(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NO_CONTENT
                )
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).pullAndInstall(
            "vieus/sshfs",
            "sshfs",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                .add("Name", "network")
                .add("Description", "")
                .add("Value", "host")
            ).build()
        );
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/disable",
                    req -> req.getRequestLine().getUri()
                    .endsWith("/sshfs/disable")
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        plugin.disable();
    }

    /**
     * RtPlugin.disable() must throw UnexpectedResponseException
     * if service responds with 404.
     * @throws Exception If something goes wrong.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void failsToDisableItselfWhenNotInstalled() throws Exception {
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NOT_FOUND
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/disable",
                    req -> req.getRequestLine().getUri()
                    .endsWith("/sshfs/disable")
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        plugin.disable();
    }

    /**
     * RtPlugin upgrade with properties ok.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void upgradeOk() throws Exception {
        new ListedPlugins(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NO_CONTENT
                )
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).pullAndInstall(
            "vieus/sshfs",
            "sshfs",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                    .add("Name", "network")
                    .add("Description", "")
                    .add("Value", "host")
            ).build()
        );
        final JsonArray properties = Json.createArrayBuilder().add(
            Json.createObjectBuilder()
                .add("Name", "mount")
                .add("Description", "")
                .add("Value", "/data")
        ).build();
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NO_CONTENT
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/upgrade?remote=test",
                    req -> req.getRequestLine().getUri()
                        .endsWith("/sshfs/upgrade?remote=test")
                ),
                new Condition(
                    "upgrade() must send JsonArray request body",
                    req -> {
                        JsonObject payload =
                            new ArrayPayloadOf(req).next();
                        return "mount".equals(payload.getString("Name"))
                            && "/data".equals(payload.getString("Value"));
                    }
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        plugin.upgrade("test", properties);
    }

    /**
     * RtPlugin upgrade throws UnexpectedResponseException if service
     * responds with 404.
     * @throws Exception If something goes wrong.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void upgradeFailsPluginNotInstalled() throws Exception {
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NOT_FOUND
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/upgrade?remote=test",
                    req -> req.getRequestLine().getUri()
                        .endsWith("/sshfs/upgrade?remote=test")
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        final JsonArray properties = Json.createArrayBuilder().add(
            Json.createObjectBuilder()
                .add("Name", "mount")
                .add("Description", "")
                .add("Value", "/data")
        ).build();
        plugin.upgrade("test", properties);
    }

    /**
     * RtPlugin push to repository ok.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void pushOk() throws Exception {
        new ListedPlugins(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NO_CONTENT
                )
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).pullAndInstall(
            "vieus/sshfs",
            "sshfs",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                    .add("Name", "network")
                    .add("Description", "")
                    .add("Value", "host")
            ).build()
        );
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/push",
                    req -> req.getRequestLine().getUri()
                        .endsWith("/sshfs/push")
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        plugin.push();
    }

    /**
     * RtPlugin push throws UnexpectedResponseException if service
     * responds with 404.
     * @throws Exception If something goes wrong.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void pushFailsPluginNotInstalled() throws Exception {
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NOT_FOUND
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/push",
                    req -> req.getRequestLine().getUri()
                        .endsWith("/sshfs/push")
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        plugin.push();
    }

    /**
     * RtPlugin configure plugin.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void configureOk() throws Exception {
        new ListedPlugins(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NO_CONTENT
                )
            ),
            URI.create("http://localhost/plugins"),
            DOCKER
        ).pullAndInstall(
            "vieus/sshfs",
            "sshfs",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                    .add("Name", "network")
                    .add("Description", "")
                    .add("Value", "host")
            ).build()
        );
        final Map<String, String> options = new HashMap<>();
        options.put("DEBUG", "1");
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NO_CONTENT
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/set",
                    req -> req.getRequestLine().getUri()
                        .endsWith("/sshfs/set")
                ),
                new Condition(
                    "configure() must send String Array as request body",
                    req -> "[\"DEBUG=1\"]".equals(this.stringPayloadOf(req))
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        plugin.configure(options);
    }

    /**
     * RtPlugin configure throws UnexpectedResponseException if service
     * responds with 404.
     * @throws Exception If something goes wrong.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void configureFailsPluginNotInstalled() throws Exception {
        final Plugin plugin = new RtPlugin(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_NOT_FOUND
                ),
                new Condition(
                    "Method should be a POST",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "Resource path must be /{name}/set",
                    req -> req.getRequestLine().getUri()
                        .endsWith("/sshfs/set")
                )
            ),
            URI.create("http://localhost/plugins/sshfs"),
            DOCKER
        );
        plugin.configure(new HashMap<>());
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
