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

import java.net.URI;
import java.nio.file.Path;

import org.apache.http.client.HttpClient;

/**
 * Use this to communicate with a remote Docker API.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #68:30min Implement integration tests for RemoteDocker. We have to
 *  figure out how to create a remote Docker host and connect to it from Travis.
 *  Also, it will probably have to be paid (some machine on AWS or DO?).
 */
public final class RemoteDocker extends RtDocker {

    /**
     * Remote Docker engine. API version is 1.35 by default.
     * @param uri Remote Docker URI.
     * @param certs Path to the folder containing these 3:
     *  CA certificate (ca.pem), client certificate (cert.pem)
     *  and client key (key.pem).
     */
    public RemoteDocker(final URI uri, final Path certs) {
        this(uri, "v1.35", certs);
    }

    /**
     * Remote Docker engine.
     * @param uri Remote Docker URI.
     * @param version API version (eg. v1.35).
     * @param certs Path to the folder containing these 3:
     *  CA certificate (ca.pem), client certificate (cert.pem)
     *  and client key (key.pem).
     */
    public RemoteDocker(final URI uri, final String version, final Path certs) {
        this(new SslHttpClient(certs), uri, version);
    }

    /**
     * Remote Docker engine with custom HttpClient. Use this only
     * if you really know what you're doing!<br><br>
     *
     * API version is 1.35 by default.
     * @param client The http client to use.
     * @param uri Remote Docker URI.
     */
    public RemoteDocker(final HttpClient client, final URI uri) {
        this(client, uri, "v1.35");
    }

    /**
     * Remote Docker engine with a custom HttpClient. Use this only
     * if you really know what you're doing!
     *
     * @param client The http client to use.
     * @param uri Remote Docker URI.
     * @param version API version (eg. v1.35).
     */
    public RemoteDocker(
        final HttpClient client, final URI uri, final String version
    ) {
        super(client, URI.create(uri.toString() + "/" + version));
    }
}
