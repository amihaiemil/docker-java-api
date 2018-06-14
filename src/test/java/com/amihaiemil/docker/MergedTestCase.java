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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for {@link Merged}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.2
 */
public final class MergedTestCase {
    
    /**
     * {@link Merged} can combine some JsonObjects.
     */
    @Test
    public void mergesObjects() {
        final JsonObject first = Json.createObjectBuilder()
            .add("firstName", "John")
            .add("age", 23)
            .build();
        final JsonObject second = Json.createObjectBuilder()
            .add("lastName", "George")
            .add("job", "developer")
            .build();

        final JsonObject expected = Json.createObjectBuilder()
            .add("firstName", "John")
            .add("lastName", "George")
            .add("age", 23)
            .add("job", "developer")
            .build();
        MatcherAssert.assertThat(
            new Merged(first, second),
            Matchers.equalTo(expected)
        );
    }
    
    /**
     * {@link Merged} can combine one single JsonObject.
     */
    @Test
    public void mergesSingleObject() {
        final JsonObject single = Json.createObjectBuilder()
            .add("firstName", "John")
            .add("age", 23)
            .build();
        MatcherAssert.assertThat(
            new Merged(single),
            Matchers.equalTo(single)
        );
    }
    
    /**
     * {@link Merged} can combine one JsonObject and another empty one.
     */
    @Test
    public void mergesWithEmptyObject() {
        final JsonObject single = Json.createObjectBuilder()
            .add("firstName", "John")
            .add("age", 23)
            .build();
        MatcherAssert.assertThat(
            new Merged(single, Json.createObjectBuilder().build()),
            Matchers.equalTo(single)
        );
    }
}
