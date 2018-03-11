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
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

/**
 * Unit tests for UnixHttpClient.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class UnixHttpClientTestCase {

    /**
     * UnixHttpClient can be instantiated with a socket File.
     */
    @Test
    public void instantiatesWithSocket() {
        MatcherAssert.assertThat(
            new UnixHttpClient(new File("/var/run/docker.sock")),
            Matchers.notNullValue()
        );
    }

    /**
     * UnixHttpClient returns its HttpParams.
     */
    @Test
    public void getsHttpParams() {
        final HttpParams params = Mockito.mock(HttpParams.class);
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(decorated.getParams()).thenReturn(params);

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(unix.getParams(), Matchers.is(params));
        Mockito.verify(
            decorated, Mockito.times(1)
        ).getParams();
    }

    /**
     * UnixHttpClient returns its ClientConnectionManager.
     */
    @Test
    public void getsClientConnectionManager() {
        final ClientConnectionManager cmngr = Mockito.mock(
            ClientConnectionManager.class
        );
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(decorated.getConnectionManager()).thenReturn(cmngr);

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(
            unix.getConnectionManager(), Matchers.is(cmngr)
        );
        Mockito.verify(
                decorated, Mockito.times(1)
        ).getConnectionManager();
    }

    /**
     * UnixHttpClient can execute the HttpUriRequest.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void executesUriRequest() throws IOException {
        final HttpUriRequest req = Mockito.mock(HttpUriRequest.class);
        final HttpResponse resp = Mockito.mock(HttpResponse.class);
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(decorated.execute(req)).thenReturn(resp);

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(
            unix.execute(req), Matchers.is(resp)
        );
        Mockito.verify(
            decorated, Mockito.times(1)
        ).execute(req);
    }

    /**
     * UnixHttpClient can execute the HttpUriRequest with the given
     * context.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void executesUriRequestWithContext() throws IOException {
        final HttpUriRequest req = Mockito.mock(HttpUriRequest.class);
        final HttpContext context = Mockito.mock(HttpContext.class);
        final HttpResponse resp = Mockito.mock(HttpResponse.class);
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(
            decorated.execute(req, context)
        ).thenReturn(resp);

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(
            unix.execute(req, context), Matchers.is(resp)
        );
        Mockito.verify(
            decorated, Mockito.times(1)
        ).execute(req, context);
    }

    /**
     * UnixHttpClient can execute the HttpRequest with the given host.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void executesRequestWithHost() throws IOException {
        final HttpHost host = new HttpHost("127.0.0.1");
        final HttpRequest req = Mockito.mock(HttpRequest.class);
        final HttpResponse resp = Mockito.mock(HttpResponse.class);
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(
            decorated.execute(host, req)
        ).thenReturn(resp);

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(
            unix.execute(host, req), Matchers.is(resp)
        );
        Mockito.verify(
            decorated, Mockito.times(1)
        ).execute(host, req);
    }

    /**
     * UnixHttpClient can execute the HttpRequest with the given host
     * and context.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void executesRequestWithHostAndContext() throws IOException {
        final HttpHost host = new HttpHost("127.0.0.1");
        final HttpRequest req = Mockito.mock(HttpRequest.class);
        final HttpContext context = Mockito.mock(HttpContext.class);
        final HttpResponse resp = Mockito.mock(HttpResponse.class);
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(
                decorated.execute(host, req, context)
        ).thenReturn(resp);

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(
            unix.execute(host, req, context), Matchers.is(resp)
        );
        Mockito.verify(
            decorated, Mockito.times(1)
        ).execute(host, req, context);
    }

    /**
     * UnixHttpClient can execute the HttpUriRequest with the given
     * response handler.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void executesUriRequestWithHandler() throws IOException {
        final HttpUriRequest req = Mockito.mock(HttpUriRequest.class);
        final ResponseHandler<String> handler = Mockito.mock(
            ResponseHandler.class
        );
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(
            decorated.execute(req, handler)
        ).thenReturn("executed");

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(
            unix.execute(req, handler), Matchers.equalTo("executed")
        );
        Mockito.verify(
            decorated, Mockito.times(1)
        ).execute(req, handler);
    }

    /**
     * UnixHttpClient can execute the HttpUriRequest with the given
     * response handler and context.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void executesUriRequestWithHandlerAndContext() throws IOException {
        final HttpUriRequest req = Mockito.mock(HttpUriRequest.class);
        final ResponseHandler<String> handler = Mockito.mock(
            ResponseHandler.class
        );
        final HttpContext context = Mockito.mock(HttpContext.class);
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(
            decorated.execute(req, handler, context)
        ).thenReturn("executed");

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(
            unix.execute(req, handler, context), Matchers.equalTo("executed")
        );
        Mockito.verify(
            decorated, Mockito.times(1)
        ).execute(req, handler, context);
    }


    /**
     * UnixHttpClient can execute the HttpRequest with the given host and
     * response handler.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void executesRequestWithHostAndHandler() throws IOException {
        final HttpHost host = new HttpHost("127.0.0.1");
        final HttpRequest req = Mockito.mock(HttpRequest.class);
        final ResponseHandler<String> handler = Mockito.mock(
            ResponseHandler.class
        );
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(
            decorated.execute(host, req, handler)
        ).thenReturn("executed");

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(
            unix.execute(host, req, handler), Matchers.equalTo("executed")
        );
        Mockito.verify(
            decorated, Mockito.times(1)
        ).execute(host, req, handler);
    }

    /**
     * UnixHttpClient can execute the HttpRequest with the given host,
     * response handler and context.
     * @throws IOException If something goes wrong.
     */
    @Test
    public void executesRequestWithHostHandlerAndContext() throws IOException {
        final HttpHost host = new HttpHost("127.0.0.1");
        final HttpRequest req = Mockito.mock(HttpRequest.class);
        final ResponseHandler<String> handler = Mockito.mock(
            ResponseHandler.class
        );
        final HttpContext context = Mockito.mock(HttpContext.class);
        final HttpClient decorated = Mockito.mock(HttpClient.class);
        Mockito.when(
            decorated.execute(host, req, handler, context)
        ).thenReturn("executed");

        final HttpClient unix = new UnixHttpClient(decorated);
        MatcherAssert.assertThat(
            unix.execute(host, req, handler, context),
            Matchers.equalTo("executed")
        );
        Mockito.verify(
            decorated, Mockito.times(1)
        ).execute(host, req, handler, context);
    }
}
