package com.amihaiemil.docker;

import org.apache.http.client.HttpClient;

import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;

/**
 * Runtime {@link Volume}.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 */
final class RtVolume extends JsonResource implements Volume {

    /**
     * Apache HttpClient which sends the requests.
     */
    private final HttpClient client;

    /**
     * Base URI.
     */
    private final URI baseUri;

    /**
     * Docker API.
     */
    private final Docker docker;

    /**
     * Ctor.
     * @param rep JsonObject representation of this Volume.
     * @param client The http client.
     * @param uri The URI for this image.
     * @param dkr The docker entry point.
     * @checkstyle ParameterNumber (5 lines)
     */
    RtVolume(
            final JsonObject rep, final HttpClient client,
            final URI uri, final Docker dkr
    ) {
        super(rep);
        this.client = client;
        this.baseUri = uri;
        this.docker = dkr;
    }

    @Override
    public JsonObject inspect()
        throws IOException, UnexpectedResponseException {
        return new Inspection(this.client, this.baseUri.toString());
    }
}
