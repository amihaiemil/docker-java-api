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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

/**
 * Unit tests for {@link RegistryConfigAuth}.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 */
public final class RegistryConfigAuthTestCase {

    /**
     * Correctly encodes identity token to Base64 with key registry URI.
     */
    @Test
    public void correctlyEncodesToken() {
        final String token = "abc123";
        final URI registryUri = URI.create("https://index.docker.io/v1/");
        MatcherAssert.assertThat(
            "Encoded Base64 strings should match",
            new RegistryConfigAuth(
                registryUri,
                token
            ).encoded(),
            new IsEqual<>(
                Base64.getEncoder().encodeToString(
                    Json.createObjectBuilder()
                        .add(registryUri.toString(),
                            Json.createObjectBuilder()
                                .add("identitytoken", token)
                        )
                        .build().toString()
                        .getBytes(StandardCharsets.UTF_8)
                )
            )
        );
    }

    /**
     * Correctly encodes credentials to Base64 with key registry URI.
     */
    @Test
    public void correctlyEncodesCredentials() {
        final String user = "bob";
        final String pwd = "123";
        final URI registryUri = URI.create("docker.example.com");
        MatcherAssert.assertThat(
            "Encoded Base64 strings should match",
            new RegistryConfigAuth(
                registryUri,
                user,
                pwd
            ).encoded(),
            new IsEqual<>(
                Base64.getEncoder().encodeToString(
                    Json.createObjectBuilder()
                        .add(registryUri.toString(),
                            Json.createObjectBuilder()
                                .add("username", user)
                                .add("password", pwd)
                        )
                        .build().toString()
                        .getBytes(StandardCharsets.UTF_8)
                )
            )
        );
    }

    /**
     * Provides correct header name.
     */
    @Test
    public void correctHeaderName() {
        final String token = "abc123";
        final URI registryUri = URI.create("https://index.docker.io/v1/");
        MatcherAssert.assertThat(
            "The header name should be 'X-Registry-Config'",
            new RegistryConfigAuth(
                registryUri,
                token
            ).headerName(),
            new IsEqual<>("X-Registry-Config")
        );
    }

}
