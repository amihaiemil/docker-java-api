package com.amihaiemil.docker;

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import java.io.IOException;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for {@link RtVolumes}.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 * @checkstyle MethodName (500 lines)
 */
public final class RtVolumesTestCase {

    /**
     * Mock docker.
     */
    private static final Docker DOCKER = Mockito.mock(Docker.class);

    /**
     * RtVolumes.prune() sends correct request and exist successfully on
     * response code 200.
     * @throws Exception If an error occurs.
     */
    @Test
    public void prunesOk() throws Exception {
        new ListedVolumes(
            new AssertRequest(
                new Response(HttpStatus.SC_OK),
                new Condition(
                    "prune() must send a POST request",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "prune() resource URL must be '/volumes/prune'",
                    req -> req.getRequestLine()
                        .getUri().endsWith("/volumes/prune")
                )
            ),
            URI.create("http://localhost/volumes"),
            DOCKER
        ).prune();
    }

    /**
     * RtVolumes.prune() must throw UnexpectedResponseException if service
     * responds with 500.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void pruneThrowsErrorOnResponse500() throws Exception {
        new ListedVolumes(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost/volumes"),
            DOCKER
        ).prune();
    }

    /**
     * RtVolumes.create() must send a correct POST request sends
     * and exist successfully on response code 201.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createOk() throws Exception {
        final Volume volume = new ListedVolumes(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_CREATED,
                    Json.createObjectBuilder()
                        .add("Name", "test")
                        .build().toString()
                ),
                new Condition(
                    "create() must send a POST HTTP request",
                    req -> "POST".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "create() must send the request to the create url",
                    req -> "http://localhost/volumes/create".equals(
                        req.getRequestLine().getUri()
                    )
                )
            ),
            URI.create("http://localhost/volumes"),
            DOCKER
        ).create("test");
        MatcherAssert.assertThat(
            "could not return correct Volume name",
            volume.getString("Name"),
            new IsEqual<>("test")
        );
    }

    /**
     * RtVolumes.create() must send a correct POST request sends
     * and exist successfully on response code 201.
     * @throws Exception If something goes wrong.
     * @checkstyle ExecutableStatementCount (100 lines)
     */
    @Test
    public void createWithParametersOk() throws Exception {
        final JsonObject labels = Json.createObjectBuilder()
            .add("label1", "label one")
            .add("label2", "label two").build();
        final Volume volume =
            new ListedVolumes(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_CREATED,
                        Json.createObjectBuilder()
                            .add("Name", "testwithparameters")
                            .build().toString()
                    ),
                    new Condition(
                        "create() must send a POST HTTP request",
                        req -> "POST".equals(req.getRequestLine().getMethod())
                    ),
                    new Condition(
                        "create() must send the request to the create url",
                        req -> "http://localhost/volumes/create".equals(
                            req.getRequestLine().getUri()
                        )
                    ),
                    new Condition(
                        "create() must send Json body request",
                        req -> {
                            final JsonObject payload = new PayloadOf(req);
                            // @checkstyle LineLength (2 lines)
                            return payload.getString("Driver").equals("custom")
                                && payload.getJsonObject("Labels").getString("label1").equals(labels.getString("label1"));
                        }
                    )
                ),
                URI.create("http://localhost/volumes"),
                DOCKER
            ).create(
                "testwithparameters",
                Json.createObjectBuilder()
                    .add("Driver", "custom")
                    .add("Labels", labels)
                    .build()
            );
        MatcherAssert.assertThat(
            "could not return correct Volume name",
            volume.getString("Name"),
            new IsEqual<>("testwithparameters")
        );
    }

    /**
     * RtVolumes.create() must throw IOException if response is empty.
     * @throws Exception The IOException.
     */
    @Test(expected = IOException.class)
    public void createThrowsErrorOnEmptyResponse() throws Exception {
        new ListedVolumes(
            new AssertRequest(
                new Response(HttpStatus.SC_CREATED)
            ),
            URI.create("http://localhost/volumes"),
            DOCKER
        ).create("test");
    }

    /**
     * RtVolumes.create() must throw UnexpectedResponseException if service
     * responds with 500.
     * @throws Exception The UnexpectedResponseException.
     */
    @Test(expected = UnexpectedResponseException.class)
    public void createThrowsErrorOnResponse500() throws Exception {
        new ListedVolumes(
            new AssertRequest(
                new Response(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            ),
            URI.create("http://localhost/volumes"),
            DOCKER
        ).create("test");
    }

    /**
     * RtVolumes can return its Docker parent.
     */
    @Test
    public void returnsDocker() {
        MatcherAssert.assertThat(
            new ListedVolumes(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_OK,
                        Json.createArrayBuilder().build().toString()
                    )
                ),
                URI.create("http://localhost"),
                DOCKER
            ).docker(),
            new IsEqual<>(DOCKER)
        );
    }

}
