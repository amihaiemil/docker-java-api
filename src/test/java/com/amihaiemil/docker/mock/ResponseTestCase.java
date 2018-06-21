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

import javax.json.Json;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for {@link Response}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.2
 */
public final class ResponseTestCase {
    
    /**
     * {@link Response} can return its String representation.
     */
    @Test
    public void toStringWorks() {
        MatcherAssert.assertThat(
            new Response(
                HttpStatus.SC_OK,
                Json.createArrayBuilder()
                    .add(
                        Json.createObjectBuilder()
                            .add("Id", "sha256:e216a057b1cb1efc1")
                    ).add(
                        Json.createObjectBuilder()
                            .add("Id", "sha256:3e314f95dcace0f5e")
                    ).build().toString()
            ).toString(),
            Matchers.allOf(
                Matchers.startsWith("HTTP/1.1 200 REASON"),
                Matchers.endsWith(
                    // @checkstyle LineLength (1 lines)
                    "[{\"Id\":\"sha256:e216a057b1cb1efc1\"},{\"Id\":\"sha256:3e314f95dcace0f5e\"}]"
                ),
                Matchers.containsString("Content-Length: 69"),
                Matchers.containsString("" + (char) 0x0D + (char) 0x0A)
            )
        );
    }
    
}
