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
package com.amihaiemil.docker.mock;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Tests for {@link AssertRequest}.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class AssertRequestTestCase {
    /**
     * Should return the given response if the request meets the given
     * condition.
     * 
     * @throws Exception Unexpected.
     */
    @Test
    public void returnResponseIfRequestMeetsCondition() throws Exception {
        final HttpResponse response = new BasicHttpResponse(
            new BasicStatusLine(
                new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK"
            )
        );
        MatcherAssert.assertThat(
            new AssertRequest(
                response,
                new Condition(
                    "",
                    // @checkstyle LineLength (1 line)
                    r -> "http://some.test.com".equals(r.getRequestLine().getUri())
                )
            ).execute(new HttpGet("http://some.test.com")),
            Matchers.is(response)
        );
    }

    /**
     * Should fail if the http request does not meet the given condition.
     *
     * @throws Exception Unexpected.
     */
    @Test(expected = AssertionError.class)
    public void failIfRequestDoesNotMeetCondition() throws Exception {
        new AssertRequest(
            null,
            new Condition(
                "",
                r -> "http://some.test.com".equals(r.getRequestLine().getUri())
            )
        ).execute(new HttpGet("http://test.com"));
    }

    /**
     * The failure message should be equal to the one specified in the
     * condition.
     *
     * @throws Exception Unexpected.
     */
    @Test
    public void failureMsg() throws Exception {
        final String msg = "Test message";
        try {
            new AssertRequest(
                null,
                new Condition(
                    msg,
                    // @checkstyle LineLength (1 line)
                    r -> "http://some.test.com".equals(r.getRequestLine().getUri())
                )
            ).execute(new HttpGet("http://test.com"));
        } catch (final AssertionError error) {
            MatcherAssert.assertThat(
                "The failure message must be equal to the one given.",
                error.getMessage(),
                Matchers.is(msg)
            );
        }
    }
}
