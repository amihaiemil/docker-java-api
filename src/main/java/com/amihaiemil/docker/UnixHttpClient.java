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

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;

/**
 * An HttpClient which works over a UnixSocket.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ParameterNumber (150 lines)
 * @todo #29:30min Implement a ConnectionSocketFactory which builds
 *  UnixSocket objects.
 */
public final class UnixHttpClient implements HttpClient {

    /**
     * Decorated HttpClient.
     */
    private HttpClient client;

    /**
     * Ctor.
     * @param socketFile Path to the unix socket on disk.
     */
    public UnixHttpClient(final String socketFile) {
        this(HttpClientBuilder.create().build());
    }

    /**
     * Ctor.
     * @param client Decorated HttpClient.
     */
    public UnixHttpClient(final HttpClient client) {
        this.client = client;
    }

    @Override
    public HttpParams getParams() {
        return this.client.getParams();
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return this.client.getConnectionManager();
    }

    @Override
    public HttpResponse execute(
        final HttpUriRequest httpUriRequest
    ) throws IOException {
        return this.client.execute(httpUriRequest);
    }

    @Override
    public HttpResponse execute(
        final HttpUriRequest httpUriRequest, final HttpContext httpContext
    ) throws IOException {
        return this.client.execute(httpUriRequest, httpContext);
    }

    @Override
    public HttpResponse execute(
        final HttpHost httpHost, final HttpRequest httpRequest
    ) throws IOException {
        return this.client.execute(httpHost, httpRequest);
    }

    @Override
    public HttpResponse execute(
        final HttpHost httpHost,
        final HttpRequest httpRequest,
        final HttpContext httpContext
    ) throws IOException {
        return this.client.execute(httpHost, httpRequest, httpContext);
    }

    @Override
    public <T> T execute(
        final HttpUriRequest httpUriRequest,
        final ResponseHandler<? extends T> responseHandler
    ) throws IOException {
        return this.client.execute(httpUriRequest, responseHandler);
    }

    @Override
    public <T> T execute(
        final HttpUriRequest httpUriRequest,
        final ResponseHandler<? extends T> responseHandler,
        final HttpContext httpContext
    ) throws IOException {
        return this.client.execute(
            httpUriRequest, responseHandler, httpContext
        );
    }

    @Override
    public <T> T execute(
        final HttpHost httpHost,
        final HttpRequest httpRequest,
        final ResponseHandler<? extends T> responseHandler
    ) throws IOException {
        return this.client.execute(httpHost, httpRequest, responseHandler);
    }

    @Override
    public <T> T execute(
        final HttpHost httpHost,
        final HttpRequest httpRequest,
        final ResponseHandler<? extends T> responseHandler,
        final HttpContext httpContext
    ) throws IOException {
        return this.client.execute(
            httpHost, httpRequest, responseHandler, httpContext
        );
    }
}
