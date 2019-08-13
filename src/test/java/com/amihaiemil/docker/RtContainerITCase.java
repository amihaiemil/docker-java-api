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
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

/**
 * Integration tests for {@link RtContainer}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class RtContainerITCase {

    /**
     * {@link RtContainer} can rename the Docker container it represents.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void renamesContainer() throws Exception {
        final Container container = new LocalDocker(
            new File("/var/run/docker.sock")
        ).containers().create("Toomes", "hello-world");
        MatcherAssert.assertThat(
            container.inspect().getString("Name"),
            Matchers.equalTo("/Toomes")
        );
        container.rename("Fury");
        MatcherAssert.assertThat(
            container.inspect().getString("Name"),
            Matchers.equalTo("/Fury")
        );
        container.remove();
    }

    /**
     * {@link RtContainer} can start/stop Docker container it represents.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void startStopContainer() throws Exception {
        final Container container = new LocalDocker(
            new File("/var/run/docker.sock")
        ).containers().create("TestStart", this.containerJsonObject());
        container.start();
        MatcherAssert.assertThat(
            this.runningState(container),
            new IsEqual<>(true)
        );
        container.stop();
        container.remove();
    }

    /**
     * {@link RtContainer} can kill Docker container it represents.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void killContainer() throws Exception {
        final Container container = new LocalDocker(
            new File("/var/run/docker.sock")
        ).containers().create("TestKill", this.containerJsonObject());
        container.start();
        MatcherAssert.assertThat(
            this.runningState(container),
            new IsEqual<>(true)
        );
        container.kill();
        MatcherAssert.assertThat(
            this.runningState(container),
            new IsEqual<>(false)
        );
        container.remove();
    }

    /**
     * {@link RtContainer} can restart Docker container it represents.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void restartContainer() throws Exception {
        final Container container = new LocalDocker(
            new File("/var/run/docker.sock")
        ).containers().create("TestRestart", this.containerJsonObject());
        container.start();
        MatcherAssert.assertThat(
            this.runningState(container),
            new IsEqual<>(true)
        );
        container.restart();
        MatcherAssert.assertThat(
            this.runningState(container),
            new IsEqual<>(true)
        );
        container.stop();
        container.remove();
    }

    /**
     * Create Container Json Object to support stop/restart/kill commands.
     * @return Json Object representing Docker Container
     */
    private JsonObject containerJsonObject() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("Image", "ubuntu");
        json.add("Tty", true);
        json.add("Cmd", "bash");
        return json.build();
    }

    /**
     * {@link RtContainer} can pause Docker container it represents.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void pauseContainer() throws Exception {
        final Container container = new LocalDocker(
                new File("/var/run/docker.sock")
        ).containers().create("TestPause", this.containerJsonObject());
        container.start();
        container.pause();
        MatcherAssert.assertThat(
                this.pausedState(container),
                new IsEqual<>(true)
        );
        container.stop();
        container.remove();
    }

    /**
     * {@link RtContainer} can unpause Docker container it represents.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void unpauseContainer() throws Exception {
        final Container container = new LocalDocker(
                new File("/var/run/docker.sock")
        ).containers().create("TestUnpause", this.containerJsonObject());
        container.start();
        container.pause();
        
        MatcherAssert.assertThat(
                this.pausedState(container),
                new IsEqual<>(true)
        );
        container.unpause();
        MatcherAssert.assertThat(
                this.runningState(container),
                new IsEqual<>(true)
        );
        container.stop();
        container.remove();
    }

    /**
     * Inspect Container and check running state.
     * @param container Docker Container.
     * @return Running state.
     * @throws IOException If something goes wrong.
     */
    private boolean runningState(final Container container) throws IOException {
        return container.inspect().getJsonObject("State")
            .getBoolean("Running");
    }

    /**
     * Inspect Container and check paused state.
     * @param container Docker Container.
     * @return Running state.
     * @throws IOException If something goes wrong.
     */
    private boolean pausedState(final Container container) throws IOException {
        return container.inspect().getJsonObject("State")
                .getBoolean("Paused");
    }

}
