/**
 * Copyright (c) 2019, Mihai Emil Andronache
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.params.BasicHttpParams;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Unit tests for {@link UserAgentRequestHeader}.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.8
 */
public final class UserAgentRequestHeaderTestCase {

    /**
     * UserAgentRequestHeader adds correct header with current version.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void correctUserAgentHeader() throws Exception {
        final RequestLine mockRequestLine = Mockito.mock(RequestLine.class);
        Mockito.when(mockRequestLine.getMethod()).thenReturn("GET");
        final HttpRequest mockRequest = Mockito.mock(HttpRequest.class);
        Mockito.when(mockRequest.getRequestLine()).thenReturn(mockRequestLine);
        Mockito.when(mockRequest.getParams()).thenReturn(new BasicHttpParams());

        final UserAgentRequestHeader header = new UserAgentRequestHeader();
        header.process(mockRequest, null);

        final ArgumentCaptor<Header> headerCaptor =
            ArgumentCaptor.forClass(Header.class);
        Mockito.verify(mockRequest).addHeader(headerCaptor.capture());
        MatcherAssert.assertThat(
            "Header name must be User-Agent",
            headerCaptor.getValue().getName(),
            new IsEqual<>("User-Agent")
        );
        final Pattern pattern =
            Pattern.compile("(?<=docker-java-api.\\/.)(.*)(?=.See)");
        final Matcher matcher =
            pattern.matcher(headerCaptor.getValue().getValue());
        MatcherAssert.assertThat(
            "Header value must have version in it",
            matcher.find(),
            new IsEqual<>(true)
        );
    }
}
