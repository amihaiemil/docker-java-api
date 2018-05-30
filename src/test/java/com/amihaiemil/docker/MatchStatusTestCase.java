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

import com.amihaiemil.docker.mock.Response;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for {@link MatchStatus}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class MatchStatusTestCase {
    
    /**
     * {@link MatchStatus} throws UnexpectedResponseException if the status
     * of the HttpResponse is not the expected one.
     * @throws Exception If something goes wrong.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void complainsOnDifferentStatus() throws Exception {
        new MatchStatus(URI.create("/test/ur"), HttpStatus.SC_OK)
            .handleResponse(new Response(HttpStatus.SC_NOT_FOUND));
    }
    
    /**
     * {@link MatchStatus} returns the HttpResponse if the status code is
     * the expected one.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void returnsResponseOnMatch() throws Exception {
        final HttpResponse resp = new Response(HttpStatus.SC_OK);
        MatcherAssert.assertThat(
            new MatchStatus(URI.create("/test/ur"), HttpStatus.SC_OK)
                .handleResponse(resp),
            Matchers.is(resp)
        );
    }
    
}
