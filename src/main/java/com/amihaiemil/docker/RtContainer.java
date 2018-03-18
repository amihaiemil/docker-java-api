package com.amihaiemil.docker;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;

/**
 * Restful Container.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #46:30min This class represents a Container. It has to implement the
 *  API's methods which are acting upon a docker Container like logs,
 *  delete, stop etc).
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
        final HttpGet inspect = new HttpGet(this.baseUri.toString() + "/json");
        final HttpResponse response = this.client.execute(inspect);
        final JsonObject info;
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            info = Json
                .createReader(response.getEntity().getContent()).readObject();
        } else {
            info = null;
        }
        inspect.releaseConnection();
        return info;
    }

    @Override
    public void start() throws IOException {
        final HttpPost start = new HttpPost(
            this.baseUri.toString() + "/start"
        );
        final HttpResponse response = this.client.execute(start);
        final int status = response.getStatusLine().getStatusCode();
        if(status!= HttpStatus.SC_NO_CONTENT) {
            throw new IllegalStateException(
                "Container#start() expected status 204, but got " + status
            );
        }
        start.releaseConnection();
    }

    @Override
    public String containerId() {
        return this.baseUri.toString().substring(
            this.baseUri.toString().lastIndexOf("/") + 1
        );
    }
}
