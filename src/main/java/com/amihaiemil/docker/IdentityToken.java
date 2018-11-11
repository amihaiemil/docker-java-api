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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Supplier;
import javax.json.Json;

/**
 * A {@link Auth} holding an Identity token.
 * <p>
 * Identity tokens are obtained after validating your {@link Credentials}
 * with a registry. However, the docker engine is capable of obtaining this
 * token transparently if you just provide {@link Credentials}.
 * <p>
 * {@link IdentityToken} is useful for cases when you have already obtained
 * a token from a previous session and you wish to reuse it.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @see <a href="https://docs.docker.com/engine/api/v1.35/#section/Authentication">Authentication</a>
 * @see <a href="https://docs.docker.com/registry/spec/auth/token/">Token Authentication Specification</a>
 * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/SystemAuth">Check auth configuration</a>
 * @since 0.0.4
 */
public final class IdentityToken implements Auth {
    /**
     * Base64-encoded JSON structure holding the identity token. 
     */
    private final Supplier<String> value;

    /**
     * Ctor.
     * @param value The token's value
     */
    public IdentityToken(final String value) {
        this.value = () -> Base64.getEncoder().encodeToString(
            Json.createObjectBuilder().add("identitytoken", value)
                .build().toString()
                .getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String encoded() {
        return this.value.get();
    }
}
