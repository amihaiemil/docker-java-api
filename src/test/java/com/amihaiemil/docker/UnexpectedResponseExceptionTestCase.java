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

import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for {@link UnexpectedResponseException}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @since 0.0.1
 * @version $Id$
 */
public final class UnexpectedResponseExceptionTestCase {

    /**
     * UnexpectedResponseException can return the actual status.
     */
    @Test
    public void returnsActualStatus() {
        MatcherAssert.assertThat(
            new UnexpectedResponseException(
                "/uri", HttpStatus.SC_NOT_FOUND,
                HttpStatus.SC_OK, Json.createObjectBuilder().build()
            ).actualStatus(),
            Matchers.equalTo(HttpStatus.SC_NOT_FOUND)
        );
    }

    /**
     * UnexpectedResponseException can return the expected status.
     */
    @Test
    public void returnsExpectedStatus() {
        MatcherAssert.assertThat(
            new UnexpectedResponseException(
                "/uri", HttpStatus.SC_NOT_FOUND,
                HttpStatus.SC_OK, Json.createObjectBuilder().build()
            ).expectedStatus(),
            Matchers.equalTo(HttpStatus.SC_OK)
        );
    }

    /**
     * UnexpectedResponseException can return the endpoing.
     */
    @Test
    public void returnsEndpoint() {
        MatcherAssert.assertThat(
            new UnexpectedResponseException(
                "/uri", HttpStatus.SC_NOT_FOUND,
                HttpStatus.SC_OK, Json.createObjectBuilder().build()
            ).endpoint(),
            Matchers.equalTo("/uri")
        );
    }

    /**
     * UnexpectedResponseException has a proper message.
     */
    @Test
    public void returnsMessage() {
        MatcherAssert.assertThat(
            new UnexpectedResponseException(
                "/uri", HttpStatus.SC_NOT_FOUND,
                HttpStatus.SC_OK, Json.createObjectBuilder().build()
            ).getMessage(),
            Matchers.equalTo(
                "Expected status 200 but got 404 when calling /uri"
            )
        );
    }

    /**
     * UnexpectedResponseException returns the payload.
     */
    @Test
    public void returnsPayload() {
        final JsonObject payload = Json.createObjectBuilder().build();
        MatcherAssert.assertThat(
            new UnexpectedResponseException(
                "/uri", HttpStatus.SC_OK,
                HttpStatus.SC_OK, payload
            ).payload(),
            Matchers.is(payload)
        );
    }
}
