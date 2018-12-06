package com.amihaiemil.docker;

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.URI;

/**
 * Unit tests for RtVolume.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 * @checkstyle MethodName (500 lines)
 */
public final class RtVolumeTestCase {

    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * RtVolume can return info about itself.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void inspectsItself() throws Exception {
        final Volume volume = new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createObjectBuilder()
                        .add("Name", "v1")
                        .add("Driver", "custom")
                        .add("Mountpoint", "/var/lib/docker/volumes/v1")
                        .add("Scope", "local")
                        .build().toString()
                ),
                new Condition(
                    "Method should be a GET",
                    req -> req.getRequestLine().getMethod().equals("GET")
                ),
                new Condition(
                    "Resource path must be /{name}",
                    req -> req.getRequestLine().getUri().endsWith("/v1")
                )
            ),
            URI.create("http://localhost:80/1.35/volumes/v1"),
            DOCKER
        );
        final JsonObject info = volume.inspect();
        MatcherAssert.assertThat(
            "Size of Json keys should be 4",
            info.keySet(),
            new IsCollectionWithSize<>(
                new IsEqual<>(4)
            )
        );
        MatcherAssert.assertThat(
            "Name value should be 'v1'",
            info.getString("Name"),
            new IsEqual<>("v1")
        );
        MatcherAssert.assertThat(
            "Driver value should be 'custom'",
            info.getString("Driver"),
            new IsEqual<>("custom")
        );
        MatcherAssert.assertThat(
            "Mountpoint value should be '/var/lib/docker/volumes/v1'",
            info.getString("Mountpoint"),
            new IsEqual<>("/var/lib/docker/volumes/v1")
        );
        MatcherAssert.assertThat(
            "Scope value should be 'local'",
            info.getString("Scope"),
            new IsEqual<>("local")
        );
    }

}
