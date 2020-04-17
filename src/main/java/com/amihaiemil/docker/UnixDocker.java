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
import java.net.URI;
import org.apache.http.client.HttpClient;

/**
 * Local Docker API. Use this when you want to communicate with the local
 * Docker engine.
 *
 * <pre>
 *     final Docker docker = new LocalUnixDocker("unix:///var/run/docker.sock");
 * </pre>
 * 
 * This implementation manages an internal pool of 10 http connections. Users
 * who wish to alter this behaviour may provide their own {@link HttpClient}
 * via the specific constructor.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class UnixDocker extends RtDocker {

    /**
     * Unix Docker engine.
     * @param unixSocket Unix socket File on disk.
     *     (most likely /var/run/docker.sock).
     */
    public UnixDocker(final File unixSocket){
        this(unixSocket, "v1.35");
    }

    /**
     * Unix Docker engine.
     * @param unixSocket Unix socket File on disk.
     *     (most likely /var/run/docker.sock).
     * @param version API version (e.g. v1.30).
     */
    public UnixDocker(final File unixSocket, final String version){
        this(new UnixHttpClient(unixSocket), version);
    }

    /**
     * Unix Docker engine.
     * <p>
     * Users may supply their own {@link HttpClient} that must register a
     * {@link UnixSocketFactory}.
     * @param client The http client to use.
     * @param version API version (e.g. v1.30).
     */
    public UnixDocker(final HttpClient client, final String version) {
        super(client, URI.create("unix://localhost:80/" + version));
    }

}
