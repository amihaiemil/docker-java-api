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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Test;
import org.mockito.internal.matchers.GreaterOrEqual;

import javax.json.JsonObject;

/**
 * Integration tests for LocalUnixDocker.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class UnixDockerITCase {

    /**
     * UnixDocker can ping the Docker Engine.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void pingsDocker() throws Exception {
        final Docker docker = new UnixDocker(
            new File("/var/run/docker.sock")
        );
        MatcherAssert.assertThat(docker.ping(), Matchers.is(Boolean.TRUE));
    }

    /**
     * Docker can return its Events unfiltered.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void returnsUnfilteredEvents() throws Exception {
        final Thread pull = new Thread(
            () -> {
                try {
                    Thread.sleep(2000);
                    final Image img = new UnixDocker(
                        new File("/var/run/docker.sock")
                    ).images().pull("hello-world", "latest");
                } catch (final IOException | InterruptedException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        );
        pull.start();
        try {
            final Events events = new UnixDocker(
                new File("/var/run/docker.sock")
            ).events();
            final JsonObject pulled = events
                .monitor()
                .limit(1)
                .collect(Collectors.toList())
                .get(0);
            System.out.println("PULLED UNFILTERED EVENT: ");
            System.out.println(pulled);
            MatcherAssert.assertThat(
                pulled.getString("status"),
                Matchers.equalTo("pull")
            );
            MatcherAssert.assertThat(
                pulled.getString("id"),
                Matchers.equalTo("hello-world:latest")
            );
        } finally {
            pull.stop();
        }
    }

    /**
     * Docker can return its Events with filtering at streaming time.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void returnsFilteredStreamOfEvents() throws Exception {
        final Thread pull = new Thread(
            () -> {
                try {
                    Thread.sleep(2000);
                    final Images images = new UnixDocker(
                        new File("/var/run/docker.sock")
                    ).images();
                    images.pull("hello-world", "latest");
                    images.pull("ubuntu", "latest");
                    images.pull("hello-world", "latest");
                    images.pull("ubuntu", "latest");
                } catch (final IOException | InterruptedException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        );
        pull.start();
        try {
            final Events events = new UnixDocker(
                new File("/var/run/docker.sock")
            ).events();
            final List<JsonObject> pulled = events
                .monitor()
                .filter(json -> json.getString("id").startsWith("ubuntu"))
                .limit(2)
                .collect(Collectors.toList());
            MatcherAssert.assertThat(
                pulled,
                Matchers.iterableWithSize(2)
            );
            MatcherAssert.assertThat(
                pulled.get(0).getString("id"),
                Matchers.equalTo("ubuntu:latest")
            );
            MatcherAssert.assertThat(
                pulled.get(1).getString("id"),
                Matchers.equalTo("ubuntu:latest")
            );
        } finally {
            pull.stop();
        }
    }

    /**
     * Docker can return its Events which are filter at request time.
     * @checkstyle LineLength (100 lines)
     * @throws Exception If something goes wrong.
     */
    @Test
    public void returnsFilteredEvents() throws Exception {
        final Thread pull = new Thread(
            () -> {
                try {
                    Thread.sleep(2000);
                    final Container started = new UnixDocker(
                        new File("/var/run/docker.sock")
                    ).images().pull("hello-world", "latest").run();
                } catch (final IOException | InterruptedException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        );
        pull.start();
        try {
            final Events events = new UnixDocker(
                new File("/var/run/docker.sock")
            ).events();
            final List<JsonObject> streamed = events
                .filter(
                    () -> {
                        final Map<String, Iterable<String>> filters = new HashMap<>();
                        filters.put("type", Arrays.asList("container"));
                        return filters;
                    }
                )
                .monitor()
                .limit(3)
                .collect(Collectors.toList());
            MatcherAssert.assertThat(
                streamed,
                Matchers.iterableWithSize(3)
            );
            for(final JsonObject event : streamed) {
                MatcherAssert.assertThat(
                    event.getString("Type"),
                    Matchers.equalTo("container")
                );
            }
            MatcherAssert.assertThat(
                streamed.get(0).getString("status"),
                Matchers.equalTo("create")
            );
            MatcherAssert.assertThat(
                streamed.get(1).getString("status"),
                Matchers.equalTo("start")
            );
            MatcherAssert.assertThat(
                streamed.get(2).getString("status"),
                Matchers.equalTo("die")
            );
        } finally {
            pull.stop();
        }
    }

    /**
     * UnixDocker can list {@link Volumes}.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void listVolumes() throws Exception {
        final Docker docker = new UnixDocker(
            Paths.get("/var/run/docker.sock").toFile()
        );
        MatcherAssert.assertThat(
            docker.volumes(),
            new IsIterableWithSize<>(new GreaterOrEqual<>(0))
        );
    }

}
