package com.amihaiemil.docker;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Supplier;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * An {@link Auth} holding a Registry Config.
 * Auth configurations for multiple registries that a build may refer to.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ImageBuild">Build an image</a>
 * @since 0.0.7
 */
public class RegistryConfigAuth implements Auth {

    /**
     * Base64-encoded JSON structure holding the regsitry config header value.
     */
    private final Supplier<String> value;

    /**
     * Ctor.
     * @param registry The registry URI.
     * @param user The username.
     * @param pwd The user's password.
     */
    public RegistryConfigAuth(final URI registry, final String user,
                              final String pwd) {
        JsonObject credentials = Json.createObjectBuilder()
            .add("username", user)
            .add("password", pwd).build();

        this.value = setValue(registry, credentials);
    }

    /**
     * Ctor.
     * @param registry The registry URI.
     * @param identityToken The Identity Token.
     */
    public RegistryConfigAuth(final URI registry, final String identityToken) {
        JsonObject token = Json.createObjectBuilder()
            .add("identitytoken", identityToken)
            .build();

        this.value = setValue(registry, token);
    }

    private Supplier<String> setValue(URI registry, JsonObject token) {
        return () -> Base64.getEncoder().encodeToString(
            Json.createObjectBuilder().add(registry.toString(), token)
                .build().toString()
                .getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String headerName() {
        return "X-Registry-Config";
    }

    @Override
    public String encoded() {
        return this.value.get();
    }
}
