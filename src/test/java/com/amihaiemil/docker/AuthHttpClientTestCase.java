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

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link AuthHttpClient}.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class AuthHttpClientTestCase {
    /**
     * Mock HttpClient that does nothing.
     */
    private static HttpClient noOpClient;

    /**
     * Mock Auth that always returns same header and value.
     */
    private static Auth dummyAuth;

    /**
     * Setup the mock http client.
     * @throws Exception If something does wrong.
     */
    @BeforeClass
    public static void setup() throws Exception {
        noOpClient = Mockito.mock(HttpClient.class);
        Mockito.when(noOpClient.execute(Mockito.any(HttpUriRequest.class)))
            .thenReturn(null);
        dummyAuth = Mockito.mock(Auth.class);
        Mockito.when(dummyAuth.headerName()).thenReturn("X-Registry-Auth");
        Mockito.when(dummyAuth.encoded()).thenReturn("123");
    }

    /**
     * Must inject the X-Registry-Auth header if absent and set it to the
     * auth's value.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void injectsHeaderIfAbsent() throws Exception {
        final HttpUriRequest request = new HttpGet();
        new AuthHttpClient(noOpClient, dummyAuth).execute(request);
        MatcherAssert.assertThat(
            request.getFirstHeader("X-Registry-Auth").getValue(),
            Matchers.is("123")
        );
    }

    /**
     * Leaves the request's header intact if it exists.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void leavesExistingHeaderAlone() throws Exception {
        final Header auth = new BasicHeader("X-Registry-Auth", "12356");
        final HttpUriRequest request = new HttpGet();
        request.setHeader(auth);
        new AuthHttpClient(noOpClient, dummyAuth).execute(request);
        MatcherAssert.assertThat(
            request.getFirstHeader("X-Registry-Auth"),
            Matchers.is(auth)
        );
    }
}
