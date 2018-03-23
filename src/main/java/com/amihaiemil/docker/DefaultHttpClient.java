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

import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * An HttpClient that works over a normal network socket.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ParameterNumber (150 lines)
 */
final class DefaultHttpClient implements HttpClient {
    /**
     * Decorated HttpClient.
     */
    private final HttpClient origin;

    /**
     * Ctor.
     */
    DefaultHttpClient() {
        this(
            HttpClients.custom()
                .setMaxConnPerRoute(10)
                .setMaxConnTotal(10)
                .build()
        );
    }

    /**
     * Ctor.
     * @param client Decorated HttpClient.
     */
    DefaultHttpClient(final HttpClient client) {
        this.origin = client;
    }
  
    @Override
    public HttpParams getParams() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    @Override
    public ClientConnectionManager getConnectionManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    @Override
    public HttpResponse execute(final HttpUriRequest request)
        throws IOException, ClientProtocolException {
        return this.origin.execute(request);
    }
  
    @Override
    public HttpResponse execute(final HttpUriRequest request,
        final HttpContext context)
        throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    @Override
    public HttpResponse execute(final HttpHost target,
        final HttpRequest request)
        throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    @Override
    public HttpResponse execute(final HttpHost target,
        final HttpRequest request, final HttpContext context)
        throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    @Override
    public <T> T execute(final HttpUriRequest request,
        final ResponseHandler<? extends T> responseHandler)
        throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    @Override
    public <T> T execute(final HttpUriRequest request,
        final ResponseHandler<? extends T> responseHandler,
        final HttpContext context) throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request,
        final ResponseHandler<? extends T> responseHandler)
        throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request,
        final ResponseHandler<? extends T> responseHandler,
        final HttpContext context) throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
