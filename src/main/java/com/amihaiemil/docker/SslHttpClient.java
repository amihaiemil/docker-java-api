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

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.function.Supplier;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

/**
 * An HttpClient that works over a normal network socket.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ParameterNumber (150 lines)
 */
final class SslHttpClient extends HttpClientEnvelope {
    /**
     * Ctor.
     * @param keys Path to the keystore.
     * @param trust Path to the truststore.
     * @param storePwd Password for the keystore.
     * @param keyPwd Passphrase for the key.
     */
    SslHttpClient(
        final Path keys, final Path trust,
        final char[] storePwd, final char[] keyPwd) {
        this(() -> {
            try {
                return HttpClients.custom()
                    .setMaxConnPerRoute(10)
                    .setMaxConnTotal(10)
                    .setSSLContext(
                        SSLContexts.custom()
                            .loadTrustMaterial(trust.toFile())
                            .loadKeyMaterial(keys.toFile(), storePwd, keyPwd)
                            .build()
                    )
                    .addInterceptorFirst(new UserAgentRequestHeader())
                    .build();
            } catch (final IOException | GeneralSecurityException ex) {
                throw new IllegalStateException(ex);
            }
        });
    }

    /**
     * Ctor.
     * @param client Decorated HttpClient.
     */
    SslHttpClient(final Supplier<HttpClient> client) {
        super(client);
    }
}
