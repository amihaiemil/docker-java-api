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
import com.amihaiemil.docker.mock.Response;
import java.io.File;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for LocalDocker.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class LocalDockerTestCase {

    /**
     * LocalDocker can be instantiated.
     */
    @Test
    public void canBeInstantiate() {
        MatcherAssert.assertThat(
            new LocalDocker(
                new File("/var/run/docker.sock")
            ),
            Matchers.notNullValue()
        );
    }

    /**
     * Ping must be TRUE if response is OK.
     * @throws Exception If an error occurs.
     */
    @Test
    public void pingTrueIfResponseIsOk() throws Exception {
        MatcherAssert.assertThat(
            new LocalDocker(
                new AssertRequest(
                    new Response(HttpStatus.SC_OK, "")
                ),
                "v1.35"
            ).ping(),
            Matchers.is(true)
        );
    }

    /**
     * Ping must be False if response is not OK.
     * @throws Exception If an error occurs.
     */
    @Test
    public void pingFalseIfResponseIsNotOk() throws Exception {
        MatcherAssert.assertThat(
            new LocalDocker(
                new AssertRequest(
                    new Response(HttpStatus.SC_NOT_FOUND, "")
                ),
                "v1.35"
            ).ping(),
            Matchers.is(false)
        );
    }
    
    /**
     * LocalDocker can return the Containers.
     */
    @Test
    public void getsContainers() {
        MatcherAssert.assertThat(
            new LocalDocker(
                new File("/var/run/docker.sock")
            ).containers(),
            Matchers.notNullValue()
        );
    }

    /**
     * LocalDocker can return the Swarm.
     */
    @Test
    public void returnsSwarm() {
        MatcherAssert.assertThat(
            new LocalDocker(
                new File("/var/run/docker.sock")
            ).swarm(),
            Matchers.notNullValue()
        );
    }

    /**
     * LocalDocker can return Images.
     */
    @Test
    public void returnsImages() {
        MatcherAssert.assertThat(
            new LocalDocker(
                new File("/var/run/docker.sock")
            ).images(),
            Matchers.notNullValue()
        );
    }

    /**
     * LocalDocker can return its HttpClient.
     */
    @Test
    public void returnsHttpClient() {
        MatcherAssert.assertThat(
            new LocalDocker(
                new File("/var/run/docker.sock")
            ).httpClient(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(UnixHttpClient.class)
            )
        );
    }

    /**
     * LocalDocker can return Volumes.
     */
    @Test
    public void returnsVolumes() {
        MatcherAssert.assertThat(
            new LocalDocker(
                new File("/var/run/docker.sock")
            ).volumes(),
            Matchers.notNullValue()
        );
    }

    /**
     * LocalDocker throws UnsupportedOperationException for Networks.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperationNetworks() {
        new LocalDocker(
            new File("/var/run/docker.sock")
        ).networks();
    }

    /**
     * LocalDocker throws UnsupportedOperationException for Exec.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperationExec() {
        new LocalDocker(
            new File("/var/run/docker.sock")
        ).exec();
    }

    /**
     * LocalDocker throws UnsupportedOperationException for Plugins.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperationPlugins() {
        new LocalDocker(
            new File("/var/run/docker.sock")
        ).plugins();
    }
}
