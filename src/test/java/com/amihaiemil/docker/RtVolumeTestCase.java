package com.amihaiemil.docker;

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.Mockito;

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

    /**
     * RtVolume.delete(false) must send a DELETE request to the volume's url.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void deleteSendsCorrectRequest() throws Exception {
        new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    "RtVolume.delete() must send a DELETE HTTP request",
                    req -> "DELETE".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "RtVolume.delete() must send the request to the volume url",
                    req -> "http://localhost/volumes/test?force=false".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/volumes/test"),
            DOCKER
        ).delete(Boolean.FALSE);
    }

    /**
     * RtVolume.delete(true) must send a DELETE request to the volume's url
     * with force param set to true.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void deleteWithForce() throws Exception {
        new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    "RtVolume.delete() must send a DELETE HTTP request",
                    req -> "DELETE".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "RtVolume.delete() must send the request to the volume url",
                    req -> "http://localhost/volumes/test?force=true".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/volumes/test"),
            DOCKER
        ).delete(Boolean.TRUE);
    }

    /**
     * RtVolume.delete(false) must throw UnexpectedResponseException if service
     * responds with 404.
     * @throws Exception The UnexpectedResponseException
     */
    @Test(expected = UnexpectedResponseException.class)
    public void deleteErrorOn404() throws Exception {
        new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_NOT_FOUND)
            ),
            URI.create("http://localhost/volumes/test"),
            DOCKER
        ).delete(Boolean.FALSE);
    }

    /**
     * RtVolume.delete(false) must throw UnexpectedResponseException if service
     * responds with 409.
     * @throws Exception The UnexpectedResponseException
     */
    @Test(expected = UnexpectedResponseException.class)
    public void deleteErrorOn409() throws Exception {
        new RtVolume(
            Json.createObjectBuilder().build(),
            new AssertRequest(
                new Response(HttpStatus.SC_CONFLICT)
            ),
            URI.create("http://localhost/volumes/test"),
            DOCKER
        ).delete(Boolean.FALSE);
    }
}
