package com.amihaiemil.docker;

import org.apache.http.client.HttpClient;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;

/**
 * Restful Container.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #26:30min This class represents a Container. It has to implement the
 *  API's methods which are acting upon a docker Container like inspect, logs,
 *  delete, start, stop etc).
 */
final class RtContainer implements Container {

    /**
     * Apache HttpClient which sends the requests.
     */
    private final HttpClient client;

    /**
     * Base URI.
     */
    private final URI baseUri;

    /**
     * Ctor.
     * @param client Given HTTP Client.
     * @param baseUri Base URI, ending with /{containerId}.
     */
    RtContainer(final HttpClient client, final URI baseUri) {
        this.client = client;
        this.baseUri = baseUri;
    }

    @Override
    public JsonObject inspect() throws IOException {
        return null;
    }

    @Override
    public String containerId() {
        return this.baseUri.toString().substring(
            this.baseUri.toString().lastIndexOf("/") + 1
        );
    }
}
