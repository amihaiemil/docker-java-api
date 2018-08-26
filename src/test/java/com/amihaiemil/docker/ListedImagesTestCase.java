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

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import java.net.URI;
import java.util.Iterator;

/**
 * Unit tests for {@link ListedImages}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
public final class ListedImagesTestCase {

    /**
     * {@link ListedImages} can iterate over them without
     * filters.
     */
    @Test
    public void iterateAll() {
        final Images all = new ListedImages(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "[{\"Id\": \"abc1\"}, {\"Id\":\"cde2\"}]"
                ),
                new Condition(
                    "iterate() must send a GET request",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "iterate() resource URL must be '/images/json'",
                    req -> req.getRequestLine()
                            .getUri().endsWith("/images/json")
                )
            ),
            URI.create("http://localhost/images"),
            Mockito.mock(Docker.class)
        );
        MatcherAssert.assertThat(all, Matchers.iterableWithSize(2));
        final Iterator<Image> itr = all.iterator();
        MatcherAssert.assertThat(
            itr.next().getString("Id"),
            Matchers.equalTo("abc1")
        );
        MatcherAssert.assertThat(
            itr.next().getString("Id"),
            Matchers.equalTo("cde2")
        );

    }

}
