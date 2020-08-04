package com.amihaiemil.docker;

import java.io.IOException;
import java.net.URI;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

public class RtExecInstance implements ExecInstance {

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


    RtExecInstance(
            final HttpClient client,
            final URI baseUri, final Docker dkr
    ) {
        this.client = client;
        this.baseUri = baseUri;
        this.docker = dkr;
    }

    @Override
    public String start(final JsonObject exec) throws IOException, UnexpectedResponseException {
        final HttpPost start = new HttpPost(
                this.baseUri.toString() + "/start"
        );
        try {
            start.setEntity(new StringEntity(exec.toString()));
            start.setHeader(new BasicHeader("Content-Type", "application/json"));
            final String string = this.client.execute(
                    start,
                    new ReadString(
                            new MatchStatus(start.getURI(), HttpStatus.SC_CREATED)
                    )
            );

            return string;
        } finally {
            start.releaseConnection();
        }
    }

    @Override
    public void resize() throws IOException, UnexpectedResponseException {

    }

    @Override
    public JsonObject inspect() throws IOException, UnexpectedResponseException {
        final HttpGet inspect = new HttpGet(
                this.baseUri.toString() + "/json"
        );
        try {
            final JsonObject json = this.client.execute(
                    inspect,
                    new ReadJsonObject(
                            new MatchStatus(inspect.getURI(), HttpStatus.SC_CREATED)
                    )
            );
            return json;
        } finally {
            inspect.releaseConnection();
        }
    }
}
