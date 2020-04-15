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

import java.net.URI;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link TcpDocker}.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle MethodName (500 lines)
 */

public final class TcpDockerTestCase {

    /**
     * Ping must be TRUE if response is OK.
     * @throws Exception If an error occurs.
     */
    @Test
    public void pingTrueIfResponseIsOk() throws Exception {
        MatcherAssert.assertThat(
            new TcpDocker(
                new AssertRequest(
                    new Response(HttpStatus.SC_OK, "")
                ),
                URI.create("http://remotedocker")
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
            new TcpDocker(
                new AssertRequest(
                    new Response(HttpStatus.SC_NOT_FOUND, "")
                ),
                URI.create("http://remotedocker")
            ).ping(),
            Matchers.is(false)
        );
    }

    /**
     * TcpDocker can return the Containers.
     */
    @Test
    public void getsContainers() {
        MatcherAssert.assertThat(
            new TcpDocker(
                Mockito.mock(HttpClient.class),
                URI.create("http://localhost")
            ).containers(),
            Matchers.notNullValue()
        );
    }

    /**
     * TcpDocker can return the Swarm.
     */
    @Test
    public void returnsSwarm() {
        MatcherAssert.assertThat(
            new TcpDocker(
                Mockito.mock(HttpClient.class),
                URI.create("http://localhost")
            ).swarm(),
            Matchers.notNullValue()
        );
    }
    
    
    /**
     * TcpDocker can return Images.
     */
    @Test
    public void returnsImages() {
        MatcherAssert.assertThat(
            new TcpDocker(
                Mockito.mock(HttpClient.class),
                URI.create("http://localhost")
            ).images(),
            Matchers.notNullValue()
        );
    }

    /**
     * UnixDocker can return its HttpClient.
     */
    @Test
    public void returnsHttpClient() {
        final HttpClient client = Mockito.mock(HttpClient.class);
        MatcherAssert.assertThat(
            new TcpDocker(
                client,
                URI.create("http://localhost")
            ).httpClient(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.sameInstance(client)
            )
        );
    }

    /**
     * UnixDocker should have AuthHttpClient.
     */
    @Test
    public void returnsAuthHttpClient() {
        MatcherAssert.assertThat(
            new TcpDocker(
                URI.create("http://localhost"),
                new Credentials("user", "pwd", "user@email.com", "server.com")
            ).httpClient(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(AuthHttpClient.class)
            )
        );
    }    
    
    /**
     * TcpDocker can return Volumes.
     */
    @Test
    public void returnsVolumes() {
        MatcherAssert.assertThat(
            new TcpDocker(
                Mockito.mock(HttpClient.class),
                URI.create("http://localhost")
            ).volumes(),
            Matchers.notNullValue()
        );
    }

    /**
     * TcpDocker can return Networks.
     */
    @Test
    public void returnsNetworks() {
        MatcherAssert.assertThat(
            new TcpDocker(
                Mockito.mock(HttpClient.class),
                URI.create("http://localhost")
            ).networks(),
            Matchers.notNullValue()
        );
    }
}
