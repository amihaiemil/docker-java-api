package com.amihaiemil.docker;

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import java.net.URI;

/**
 * Unit tests for RtDockerSystem.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.6
 * @checkstyle MethodName (500 lines)
 */
public final class RtDockerSystemTestCase {

    /**
     * Must return the same disk space usage for images, containers and
     * volumes as in json array returned by the service.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void returnsDiskSpaceUsage() throws Exception {
        long totalSpace = new RtDockerSystem(
                new AssertRequest(
                    new Response(
                        HttpStatus.SC_OK,
                        Json.createObjectBuilder()
                            .add("LayersSize", 250)
                            .add("Containers",
                                Json.createArrayBuilder()
                                    .add(Json.createObjectBuilder()
                                        .add("SizeRootFs", 50))
                            ).add("Volumes",
                            Json.createArrayBuilder()
                                    .add(
                                        Json.createObjectBuilder()
                                            .add("UsageData",
                                                Json.createObjectBuilder()
                                                    .add("Size", 200)))
                        ).build().toString()
                    )
                ),
                URI.create("http://localhost/system"),
                Mockito.mock(Docker.class)
                ).diskUsage().totalSpace();
        MatcherAssert.assertThat(
                totalSpace,
                Matchers.is(500L)
        );
    }


}
