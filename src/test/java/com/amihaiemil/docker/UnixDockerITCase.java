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
import java.io.Reader;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Test;
import org.junit.Ignore;
import org.mockito.internal.matchers.GreaterOrEqual;

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
        final Docker docker = new LocalDocker(
            new File("/var/run/docker.sock")
        );
        MatcherAssert.assertThat(docker.ping(), Matchers.is(Boolean.TRUE));
    }
    /**
     * Docker can follow the events stream. Ignored for now,
     * doesn't work yet.
     * @throws Exception If something goes wrong.
     */
    @Test
    @Ignore
    public void followsEvents() throws Exception {
        final Reader reader =  new LocalDocker(
            new File("/var/run/docker.sock")
        ).events();
        final String events = IOUtils.toString(reader);
        MatcherAssert.assertThat(
                events.trim(),
                Matchers.notNullValue()
        );
    }
    /**
     * UnixDocker can list {@link Volumes}.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void listVolumes() throws Exception {
        final Docker docker = new LocalDocker(
            Paths.get("/var/run/docker.sock").toFile()
        );
        MatcherAssert.assertThat(
            docker.volumes(),
            new IsIterableWithSize<>(new GreaterOrEqual<>(0))
        );
    }

}
