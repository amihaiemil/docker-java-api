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

import java.net.URI;
import java.nio.file.Path;

import org.apache.http.client.HttpClient;

/**
 * Use this to communicate with a remote Docker API.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ParameterNumber (150 lines)
 */
public final class RemoteTcpDocker extends RtDocker {

    /**
     * Remote Docker engine. API version is 1.35 by default.
     * @param uri Remote Docker URI.
     * @param keys Path to the keystore.
     * @param trust Path to the truststore.
     * @param storePwd Password for the keystore.
     * @param keyPwd Passphrase for the key.
     */
    RemoteTcpDocker(
        final URI uri, final Path keys, final Path trust,
        final char[] storePwd, final char[] keyPwd) {
        this(uri, "v1.35", keys, trust, storePwd, keyPwd);
    }

    /**
     * Remote Docker engine.
     * @param uri Remote Docker URI.
     * @param version API version (eg. v1.35).
     * @param keys Path to the keystore.
     * @param trust Path to the truststore.
     * @param storePwd Password for the keystore.
     * @param keyPwd Passphrase for the key.
     */
    RemoteTcpDocker(
        final URI uri, final String version,
        final Path keys, final Path trust,
        final char[] storePwd, final char[] keyPwd) {
        this(
            new SslHttpClient(keys, trust, storePwd, keyPwd),
            uri, version
        );
    }

    /**
     * Remote Docker engine.
     * 
     * An insecure docker API v1.35 endpoint is assumed.
     * 
     * @param uri Remote Docker URI.
     */
    public RemoteTcpDocker(final URI uri) {
        this(new PlainHttpClient(), uri);
    }    
    
    /**
     * Remote Docker engine.
     * 
     * An insecure docker API v1.35 endpoint is assumed.
     * 
     * @param uri Remote Docker URI.
     * @param auth Remote Docker {@link Auth}
     */
    public RemoteTcpDocker(final URI uri, final Auth auth) {
        this(new AuthHttpClient(new PlainHttpClient(), auth), uri);
    }

    /**
     * Remote Docker engine. You have to configure your own HttpClient,
     * most likely with some authentication mechanism, depending on where
     * the Docker engine is on the Network. <br><br>
     *
     * By default, the API version is 1.35.
     *
     * @param client The http client to use.
     * @param uri Remote Docker URI.
     */
    public RemoteTcpDocker(final HttpClient client, final URI uri) {
        this(client, uri, "v1.35");
    }

    /**
     * Remote Docker engine. You have to configure your own HttpClient,
     * most likely with some authentication mechanism, depending on where
     * the Docker engine is on the Network.
     *
     * @param client The http client to use.
     * @param uri Remote Docker URI.
     * @param version API version (eg. v1.35).
     */
    public RemoteTcpDocker(
        final HttpClient client, final URI uri, final String version
    ) {
        super(client, URI.create(uri.toString() + "/" + version));
    }
}
