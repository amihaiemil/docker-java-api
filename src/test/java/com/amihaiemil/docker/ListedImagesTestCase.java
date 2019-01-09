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

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;

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

    /**
     * {@link ListedImages} can include filters in request to fetch images.
     */
    @Test
    public void includeFiltersInRequest() {
        final Map<String, Iterable<String>> filters = new HashMap<>();
        filters.put(
            "label",
            Arrays.asList(
                "maintainer=john@doe.org",
                "randomLabel=test"
            )
        );
        new ListedImages(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "[{\"Id\": \"abc1\"}, {\"Id\":\"cde2\"}]"
                ),
                new Condition(
                    // @checkstyle LineLength (11 lines)
                    "iterate() query parameters must include the filters provided",
                    req -> {
                        final List<NameValuePair> params = new UncheckedUriBuilder(
                            req.getRequestLine().getUri()
                        ).getQueryParams();
                        // @checkstyle BooleanExpressionComplexity (5 lines)
                        return params.size() == 1
                            && "filters".equals(params.get(0).getName())
                            && params.get(0).getValue().contains("label")
                            && params.get(0).getValue().contains("\"maintainer=john@doe.org\"")
                            && params.get(0).getValue().contains("\"randomLabel=test\"");
                    }
                )
            ),
            URI.create("http://localhost/images"),
            Mockito.mock(Docker.class),
            filters
        ).iterator();
    }

    /**
     * {@link ListedImages} can include filters added in filter(), in addition
     * to those provided via ctor, in request to fetch images.
     */
    @Test
    public void includeAddedFiltersInRequest() {
        final Map<String, Iterable<String>> initial = new HashMap<>();
        initial.put(
            "label",
            Arrays.asList(
                "maintainer=john@doe.org",
                "randomLabel=test"
            )
        );
        final Map<String, Iterable<String>> added = new HashMap<>();
        added.put("dangling", Arrays.asList("true"));
        new ListedImages(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "[{\"Id\": \"abc1\"}, {\"Id\":\"cde2\"}]"
                ),
                new Condition(
                    // @checkstyle LineLength (12 lines)
                    "iterate() query parameters must include the filters provided",
                    req -> {
                        final List<NameValuePair> params = new UncheckedUriBuilder(
                            req.getRequestLine().getUri()
                        ).getQueryParams();
                        // @checkstyle BooleanExpressionComplexity (6 lines)
                        return params.size() == 1
                            && "filters".equals(params.get(0).getName())
                            && params.get(0).getValue().contains("label")
                            && params.get(0).getValue().contains("\"maintainer=john@doe.org\"")
                            && params.get(0).getValue().contains("\"randomLabel=test\"")
                            && params.get(0).getValue().contains("\"dangling\":[\"true\"]");
                    }
                )
            ),
            URI.create("http://localhost/images"),
            Mockito.mock(Docker.class),
            initial
        ).filter(added).iterator();
    }

}
