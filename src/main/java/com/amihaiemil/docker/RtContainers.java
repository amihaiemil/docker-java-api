/**
 * Copyright (c) 2018-2019, Mihai Emil Andronache
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

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;

/**
 * Containers API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
abstract class RtContainers implements Containers {

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
     * @param client Given HTTP Client.
     * @param baseUri Base URI, ending with /containers.
     * @param dkr Docker where these Containers are from.
     */
    RtContainers(
        final HttpClient client, final URI baseUri, final Docker dkr
    ) {
        this.client = client;
        this.baseUri = baseUri;
        this.docker = dkr;
    }

    @Override
    public Container create(final String image) throws IOException {
        return this.create(
            "",
            Json.createObjectBuilder()
                .add("Image", image)
                .build()
        );
    }

    @Override
    public Container create(
        final String name, final String image
    ) throws IOException {
        return this.create(
            name,
            Json.createObjectBuilder()
                .add("Image", image)
                .build()
        );
    }

    @Override
    public Container create(final JsonObject container) throws IOException {
        return this.create("", container);
    }

    @Override
    public Container create(
        final String name, final JsonObject container
    ) throws IOException {
        final URI uri;
        if(!name.isEmpty()) {
            uri = new UncheckedUriBuilder(
                this.baseUri.toString().concat("/create")
            ).addParameter("name", name)
                .build();
        } else {
            uri = new UncheckedUriBuilder(
                this.baseUri.toString().concat("/create")
            ).build();
        }
        final HttpPost post = new HttpPost(uri);
        try {
            post.setEntity(new StringEntity(container.toString()));
            post.setHeader(new BasicHeader("Content-Type", "application/json"));
            final JsonObject json = this.client.execute(
                post,
                new ReadJsonObject(
                    new MatchStatus(post.getURI(), HttpStatus.SC_CREATED)
                )
            );
            return new RtContainer(
                new Merged(json, container),
                this.client,
                URI.create(
                    this.baseUri.toString() + "/" + json.getString("Id")
                ),
                this.docker
            );
        } finally {
            post.releaseConnection();
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
     * Get this container.<br><br>
     *
     * @param containerId Id of the Container
     * @return This container object.
     */
    @Override
    public Container get(final String containerId) {
        return new RtContainer(
            Json.createObjectBuilder().build(),
            this.client,
            URI.create(
                this.baseUri.toString() + "/" + containerId
            ),
            this.docker
        );
    }
}
