package com.amihaiemil.docker;

import java.io.IOException;
import java.net.URI;
import javax.json.JsonArray;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

/**
 * Runtime {@link Plugins}.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 */
abstract class RtPlugins implements Plugins {

    /**
     * Apache HttpClient which sends the requests.
     */
    private final HttpClient client;

    /**
     * Base URI for Networks API.
     */
    private final URI baseUri;

    /**
     * Docker API.
     */
    private final Docker docker;

    /**
     * Ctor.
     * @param client The http client.
     * @param uri The URI for this Network API.
     * @param dkr The docker entry point.
     */
    RtPlugins(final HttpClient client, final URI uri, final Docker dkr) {
        this.client = client;
        this.baseUri = uri;
        this.docker = dkr;
    }

    @Override
    public void create(final String name, final String pluginDataDir)
        throws IOException, UnexpectedResponseException {
        final HttpPost create =
            new HttpPost(
                String.format("%s/%s?name=%s",
                    this.baseUri.toString(),
                    "create",
                    name
                )
            );
        try {
            create.setEntity(
                new StringEntity(pluginDataDir)
            );
            this.client.execute(
                create,
                new MatchStatus(
                    create.getURI(),
                    HttpStatus.SC_NO_CONTENT
                )
            );
        } finally {
            create.releaseConnection();
        }
    }

    @Override
    public void pullAndInstall(final String remote, final String name,
                               final JsonArray pluginProperties)
        throws IOException, UnexpectedResponseException {
        final HttpPost pull =
            new HttpPost(
                new UncheckedUriBuilder(this.baseUri.toString().concat("/pull"))
                    .addParameter("remote", remote)
                    .addParameter("name", name)
                    .build()
            );
        try {
            pull.setEntity(
                new StringEntity(pluginProperties.toString())
            );
            this.client.execute(
                pull,
                new MatchStatus(
                    pull.getURI(),
                    HttpStatus.SC_NO_CONTENT
                )
            );
        } finally {
            pull.releaseConnection();
        }
    }
}
