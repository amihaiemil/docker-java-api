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
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for RtImage.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle MethodName (500 lines)
 */
public final class RtImageTestCase {

    /**
     * RtImage can return info about itself.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void inspectsItself() throws Exception {
        final Image image = new RtImage(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createObjectBuilder()
                        .add("Id", "456")
                        .add("Container", "cb91e48a60d01f1e27028b4")
                        .add("Commant", "bla bla")
                        .add("Os", "linux")
                        .build().toString()
                ),
                new Condition(
                    "Method should be a GET",
                    req -> req.getRequestLine().getMethod().equals("GET")
                ),
                new Condition(
                    "Resource path must be /{id}/json",
                    req -> req.getRequestLine().getUri().endsWith("/456/json")
                )
            ),
            URI.create("http://localhost:80/1.30/images/456")
        );
        final JsonObject info = image.inspect();
        MatcherAssert.assertThat(info.keySet(), Matchers.hasSize(4));
        MatcherAssert.assertThat(
            info.getString("Id"), Matchers.equalTo("456")
        );
        MatcherAssert.assertThat(
            info.getString("Container"),
            Matchers.equalTo("cb91e48a60d01f1e27028b4")
        );
        MatcherAssert.assertThat(
            info.getString("Commant"), Matchers.equalTo("bla bla")
        );
        MatcherAssert.assertThat(
            info.getString("Os"), Matchers.equalTo("linux")
        );
    }
    
    /**
     * RtImage can return its history Images.
     */
    @Test
    public void returnsHistory() {
        MatcherAssert.assertThat(
            new RtImage(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_OK,
                        Json.createArrayBuilder().build().toString()
                    )
                ),
                URI.create("http://localhost:80/1.30/images/456")
            ).history(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(Images.class)
            )
        );
    }

    /**
     * RtImage.delete() must send a DELETE request to the image's url.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void deleteSendsCorrectRequest() throws Exception {
        new RtImage(
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    "RtImages.delete() must send a DELETE HTTP request",
                    req -> "DELETE".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "RtImages.delete() must send the request to the image url",
                    req -> "http://localhost/images/test".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/images/test")
        ).delete();
    }

    /**
     * RtImage.delete() must throw UnexpectedResponseException if service
     * responds with 404.
     * @throws Exception The UnexpectedResponseException
     */
    @Test(expected = UnexpectedResponseException.class)
    public void deleteErrorOn404() throws Exception {
        new RtImage(
            new AssertRequest(
                new Response(HttpStatus.SC_NOT_FOUND)
            ),
            URI.create("http://localhost/images/test")
        ).delete();
    }

    /**
     * RtImage.delete() must throw UnexpectedResponseException if service
     * responds with 409.
     * @throws Exception The UnexpectedResponseException
     */
    @Test(expected = UnexpectedResponseException.class)
    public void deleteErrorOn409() throws Exception {
        new RtImage(
            new AssertRequest(
                new Response(HttpStatus.SC_CONFLICT)
            ),
            URI.create("http://localhost/images/test")
        ).delete();
    }

    /**
     * RtImage.delete() must throw UnexpectedResponseException if service
     * responds with 500.
     * @throws Exception The UnexpectedResponseException
     */
    @Test(expected = UnexpectedResponseException.class)
    public void deleteErrorOn500() throws Exception {
        new RtImage(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost/images/test")
        ).delete();
    }
}
