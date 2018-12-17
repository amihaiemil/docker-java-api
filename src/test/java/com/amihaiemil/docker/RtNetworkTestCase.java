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
import com.amihaiemil.docker.mock.Response;
import java.net.URI;
import javax.json.Json;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link RtNetwork}.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 * @checkstyle MethodName (500 lines)
 */
public final class RtNetworkTestCase {

    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * RtNetwork throws UnsupportedOperationException for Inspect.
     * @throws Exception If something else goes wrong.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperationInspect() throws Exception {
        new RtNetwork(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_OK)
            ),
            URI.create("http://localhost/networks/id1"),
            DOCKER
        ).inspect();
    }

    /**
     * RtNetwork throws UnsupportedOperationException for Remove.
     * @throws Exception If something else goes wrong.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperationRemove() throws Exception {
        new RtNetwork(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_OK)
            ),
            URI.create("http://localhost/networks/id1"),
            DOCKER
        ).remove();
    }

    /**
     * RtNetwork throws UnsupportedOperationException for Connect.
     * @throws Exception If something else goes wrong.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperationConnect() throws Exception {
        new RtNetwork(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_OK)
            ),
            URI.create("http://localhost/networks/id1"),
            DOCKER
        ).connect("containerId");
    }

    /**
     * RtNetwork throws UnsupportedOperationException for Disconnect.
     * @throws Exception If something else goes wrong.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperationDisconnect() throws Exception {
        new RtNetwork(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_OK)
            ),
            URI.create("http://localhost/networks/id1"),
            DOCKER
        ).disconnect("containerId");
    }

}
