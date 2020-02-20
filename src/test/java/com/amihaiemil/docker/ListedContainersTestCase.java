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
import org.apache.http.NameValuePair;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.*;

/**
 * Unit tests for {@link ListedContainers}.
 * @author Michael Lux (michi.lux@gmail.com)
 * @version $Id$
 * @since 0.0.11
 */
public final class ListedContainersTestCase {

    /**
     * {@link ListedImages} can iterate over all containers with size.
     */
    @Test
    public void iterateAll() {
        Docker docker = new LocalDocker(
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
                    "URI path must end with '/containers/json'",
                    req -> new UncheckedUriBuilder(
                        req.getRequestLine().getUri()
                    ).getPath().endsWith("/containers/json")
                ),
                new Condition(
                    "URI query param must contain all=true",
                    req -> {
                        NameValuePair queryParam = new UncheckedUriBuilder(
                            req.getRequestLine().getUri()
                        ).getQueryParams().get(0);
                        return "all".equals(queryParam.getName())
                            && "true".equals(queryParam.getValue());
                    }
                ),
                new Condition(
                    "URI query param must contain size=true",
                    req -> {
                        NameValuePair queryParam = new UncheckedUriBuilder(
                            req.getRequestLine().getUri()
                        ).getQueryParams().get(1);
                        return "size".equals(queryParam.getName())
                            && "true".equals(queryParam.getValue());
                    }
                )
            ),
            "v1.35");
        final Iterator<Container> all = docker.containers()
            .withSize(true).all();
        MatcherAssert.assertThat(
            all.next().getString("Id"),
            Matchers.equalTo("abc1")
        );
        MatcherAssert.assertThat(
            all.next().getString("Id"),
            Matchers.equalTo("cde2")
        );
        MatcherAssert.assertThat(
            all.hasNext(),
            Matchers.equalTo(false)
        );
    }

    /**
     * {@link ListedImages} can include filters in request to fetch images.
     */
    @Test
    public void includeFiltersInRequest() {
        final Map<String, Iterable<String>> filterMap = new HashMap<>();
        filterMap.put(
            "label",
            Arrays.asList(
                "maintainer=john@doe.org",
                "randomLabel=test"
            )
        );
        new LocalDocker(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "[]"
                ),
                new Condition(
                    "Query parameters must include the filters provided",
                    req -> {
                        final List<NameValuePair> params =
                            new UncheckedUriBuilder(
                                req.getRequestLine().getUri()
                            ).getQueryParams();
                        NameValuePair filtersPair = params.get(0);
                        String filters = filtersPair.getValue();
                        // @checkstyle BooleanExpressionComplexity (5 lines)
                        return params.size() == 1
                            && "filters".equals(filtersPair.getName())
                            && filters.contains("label")
                            && filters.contains("\"maintainer=john@doe.org\"")
                            && filters.contains("\"randomLabel=test\"");
                    }
                )
            ),
            "v1.35").containers().filter(filterMap).iterator();
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
        added.put("dangling", Collections.singletonList("true"));
        new LocalDocker(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "[]"
                ),
                new Condition(
                    "Query parameters must include all filters provided, "
                        + "plus size set to true",
                    req -> {
                        final List<NameValuePair> params =
                            new UncheckedUriBuilder(
                                req.getRequestLine().getUri()
                            ).getQueryParams();
                        NameValuePair sizePair = params.get(0);
                        NameValuePair filtersPair = params.get(1);
                        String filters = filtersPair.getValue();
                        // @checkstyle BooleanExpressionComplexity (8 lines)
                        return params.size() == 2
                            && "size".equals(sizePair.getName())
                            && "true".equals(sizePair.getValue())
                            && "filters".equals(filtersPair.getName())
                            && filters.contains("label")
                            && filters.contains("\"maintainer=john@doe.org\"")
                            && filters.contains("\"randomLabel=test\"")
                            && filters.contains("\"dangling\":[\"true\"]");
                    }
                )
            ),
            "v1.35"
        ).containers().filter(initial).filter(added).withSize(true).iterator();
    }

}
