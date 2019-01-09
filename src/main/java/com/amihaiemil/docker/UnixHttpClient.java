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

import org.apache.http.client.HttpClient;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.File;
import java.util.function.Supplier;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * An HttpClient which works over a UnixSocket.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ParameterNumber (150 lines)
 * @checkstyle AnonInnerLength (150 lines)
 */
final class UnixHttpClient extends HttpClientEnvelope {
    /**
     * Ctor.
     * @param socketFile Unix socket on disk.
     */
    UnixHttpClient(final File socketFile) {
        this(() -> {
            final PoolingHttpClientConnectionManager pool =
                new PoolingHttpClientConnectionManager(
                    RegistryBuilder
                        .<ConnectionSocketFactory>create()
                        .register("unix", new UnixSocketFactory(socketFile))
                        .build()
                );
            pool.setDefaultMaxPerRoute(10);
            pool.setMaxTotal(10);
            return HttpClientBuilder.create()
                .setConnectionManager(pool)
                .addInterceptorFirst(new UserAgentRequestHeader())
                .build();
        });
    }

    /**
     * Ctor.
     * @param client The http client
     */
    UnixHttpClient(final Supplier<HttpClient> client) {
        super(client);
    }
}
