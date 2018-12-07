package com.amihaiemil.docker;

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Arrays;
import java.util.HashSet;
import javax.json.Json;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ListedVolumes}.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 */
public final class ListedVolumesTestCase {

    /**
     * {@link ListedVolumes} can iterate over them without
     * filters.
     */
    @Test
    public void iterateAll() {
        final Volumes all = new ListedVolumes(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "[{\"Name\": \"abc1\"}, {\"Name\":\"cde2\"}]"
                ),
                new Condition(
                    "iterate() must send a GET request",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "iterate() resource URL must be '/volumes'",
                    req -> req.getRequestLine()
                            .getUri().endsWith("/volumes")
                )
            ),
            URI.create("http://localhost/volumes"),
            Mockito.mock(Docker.class)
        );
        MatcherAssert.assertThat(
            "There should be 2 volumes in the list",
            all,
            new IsIterableWithSize<>(
                new IsEqual<>(2)
            )
        );
        final Iterator<Volume> itr = all.iterator();
        MatcherAssert.assertThat(
            "Name should match abc1",
            itr.next().getString("Name"),
            new IsEqual<>("abc1")
        );
        MatcherAssert.assertThat(
            "Name should match cde2",
            itr.next().getString("Name"),
            new IsEqual<>("cde2")
        );
    }

    /**
     * Tests if {@link ListedVolumes} can filter volumes.
     * @throws IOException If something goes wrong.
     */
    @Ignore
    @Test
    public void iterateWithFilters() throws IOException {
        final Volumes filtered = new ListedVolumes(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    "[{\"Name\": \"fgh3\"}, {\"Name\":\"ijk4\"}]"
                ),
                new Condition(
                    "iterate() must send a GET request",
                    req -> "GET".equals(req.getRequestLine().getMethod())
                ),
                new Condition(
                    "iterate() resource URL must be '/volumes'",
                    req -> req.getRequestLine()
                            .getUri().endsWith("/volumes")
                )
            ),
            URI.create("http://localhost/volumes"),
            Mockito.mock(Docker.class),
            new HashSet<Volume>(
                Arrays.asList(
                    new RtVolume(
                        Json.createObjectBuilder().build(),
                        new AssertRequest(
                            new Response(
                                HttpStatus.SC_OK,
                                "[{\"Name\": \"ijk4\"}"
                            ),
                            new Condition(
                                "iterate() must send a GET request",
                                req -> "GET".equals(
                                    req.getRequestLine().getMethod()
                                )
                            ),
                            new Condition(
                                "iterate() resource URL must be '/volumes'",
                                req -> req.getRequestLine()
                                        .getUri().endsWith("/volumes")
                            )
                        ),
                        URI.create("http://localhost/volumes"),
                        Mockito.mock(Docker.class)
                    )
                )
            )
        );
        MatcherAssert.assertThat(
            "Wrong volume returned",
            filtered.iterator().next().getString("Name"),
            new IsEqual<>("ijk4")
        );
    }
}
