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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * An {@link Auth} holding a Registry Config.
 * Auth configurations for multiple registries that a build may refer to.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ImageBuild">Build an image</a>
 * @since 0.0.7
 */
public final class RegistryConfigAuth implements Auth {

    /**
     * All registry information.
     */
    private final Map<URI, JsonObject> registries;

    /**
     * Ctor.
     * @param registry The registry URI.
     * @param user The username.
     * @param pwd The user's password.
     */
    public RegistryConfigAuth(final URI registry, final String user,
                              final String pwd) {
        this(registry, Json.createObjectBuilder()
            .add("username", user)
            .add("password", pwd).build());
    }

    /**
     * Ctor.
     * @param registry The registry URI.
     * @param identityToken The Identity Token.
     */
    public RegistryConfigAuth(final URI registry, final String identityToken) {
        this(registry, Json.createObjectBuilder()
            .add("identitytoken", identityToken)
            .build());
    }

    /**
     * Private Ctor.
     * @param registry The registry URI.
     * @param data The Json Object representing Auth Config.
     */
    private RegistryConfigAuth(final URI registry, final JsonObject data) {
        this(Collections.singletonMap(registry, data));
    }

    /**
     * Private Ctor.
     * @param registries The registries URI and.
     */
    public RegistryConfigAuth(final Map<URI, JsonObject> registries) {
        this.registries = registries;
    }

    @Override
    public String headerName() {
        return "X-Registry-Config";
    }

    @Override
    public String encoded() {
        final JsonObjectBuilder bldr = Json.createObjectBuilder();
        this.registries.forEach(
            (registry, value) -> bldr.add(registry.toString(), value)
        );
        return Base64.getEncoder().encodeToString(
            bldr.build().toString().getBytes(StandardCharsets.UTF_8)
        );
    }
}
