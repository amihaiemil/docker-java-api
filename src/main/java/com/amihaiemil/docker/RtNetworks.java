/**
 * Copyright (c) 2018, Mihai Emil Andronache
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1)Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2)Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3)Neither the name of docker-java-api nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.amihaiemil.docker;

import java.io.IOException;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * Runtime {@link Networks}.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
abstract class RtNetworks implements Networks {
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
    RtNetworks(final HttpClient client, final URI uri, final Docker dkr) {
        this.client = client;
        this.baseUri = uri;
        this.docker = dkr;
    }

    @Override
    public Network create(final String name)
        throws IOException, UnexpectedResponseException {
        return this.create(name, Json.createObjectBuilder().build());
    }

    @Override
    public Network create(final String name, final JsonObject filters)
        throws IOException, UnexpectedResponseException {
        final JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("Name", name);
        filters.forEach(json::add);
        return this.createNetwork(json);
    }

    @Override
    public void prune() throws IOException, UnexpectedResponseException {
        final HttpPost prune = new HttpPost(
            this.baseUri.toString().concat("/prune")
        );
        try {
            this.client.execute(
                prune,
                new MatchStatus(prune.getURI(), HttpStatus.SC_OK)
            );
        } finally {
            prune.releaseConnection();
        }
    }

    @Override
    public Docker docker() {
        return this.docker;
    }

    /**
     * Get the (protected) HttpClient for subclasses.
     * @return HttpClient.
     */
    HttpClient client() {
        return this.client;
    }

    /**
     * Get the (protected) base URI for subclasses.
     * @return URI.
     */
    URI baseUri() {
        return this.baseUri;
    }

    /**
     * Create network using JsonObjectBuilder.
     * @param json Json Object Builder object.
     * @return The created Network.
     * @throws IOException If something goes wrong.
     */
    private Network createNetwork(final JsonObjectBuilder json)
        throws IOException {
        final HttpPost create =
            new HttpPost(
                String.format("%s/%s", this.baseUri.toString(), "create")
            );
        try {
            create.setEntity(
                new StringEntity(
                    json.build().toString(), ContentType.APPLICATION_JSON
                )
            );
            final JsonObject createResult = this.client.execute(
                create,
                new ReadJsonObject(
                    new MatchStatus(
                        create.getURI(),
                        HttpStatus.SC_CREATED
                    )
                )
            );
            if (!createResult.isEmpty()) {
                return new RtNetwork(createResult,
                    this.client,
                    URI.create(
                        String.format("%s/%s", this.baseUri.toString(),
                            createResult.getString("Id"))
                    ),
                    this.docker
                );
            } else {
                throw new IOException(
                    "Got empty response from Networks.create() method"
                );
            }
        } finally {
            create.releaseConnection();
        }
    }
}
