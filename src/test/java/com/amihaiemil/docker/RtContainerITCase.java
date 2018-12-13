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

import java.io.File;
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
            container.inspect().getJsonObject("State").getBoolean("Running"),
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
            container.inspect().getJsonObject("State").getBoolean("Running"),
            new IsEqual<>(true)
        );
        container.kill();
        MatcherAssert.assertThat(
            container.inspect().getJsonObject("State").getBoolean("Running"),
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
            container.inspect().getJsonObject("State").getBoolean("Running"),
            new IsEqual<>(true)
        );
        container.restart();
        MatcherAssert.assertThat(
            container.inspect().getJsonObject("State").getBoolean("Running"),
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

}
