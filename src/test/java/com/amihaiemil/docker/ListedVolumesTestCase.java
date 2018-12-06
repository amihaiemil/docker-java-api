package com.amihaiemil.docker;

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Condition;
import com.amihaiemil.docker.mock.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.util.Iterator;

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
        MatcherAssert.assertThat(all, Matchers.iterableWithSize(2));
        final Iterator<Volume> itr = all.iterator();
        MatcherAssert.assertThat(
                itr.next().getString("Name"),
                Matchers.equalTo("abc1")
        );
        MatcherAssert.assertThat(
                itr.next().getString("Name"),
                Matchers.equalTo("cde2")
        );

    }
}
