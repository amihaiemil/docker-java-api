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

import org.apache.http.HttpResponse;
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
final class RtContainers implements Containers {

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
     * @param baseUri Base URI, ending with /containers.
     */
    RtContainers(final HttpClient client, final URI baseUri) {
        this.client = client;
        this.baseUri = baseUri;
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
        post.setEntity(new StringEntity(container.toString()));
        post.setHeader(new BasicHeader("Content-Type", "application/json"));
        final HttpResponse response = this.client.execute(post);
        final int status = response.getStatusLine().getStatusCode();
        if(status == HttpStatus.SC_CREATED) {
            final JsonObject json = Json
                .createReader(response.getEntity().getContent()).readObject();
            post.releaseConnection();
            return new RtContainer(
                json,
                this.client,
                URI.create(
                    this.baseUri.toString() + "/" + json.getString("Id")
                )
            );
        }
        throw new UnexpectedResponseException(
            uri.toString(), status, HttpStatus.SC_CREATED
        );
    }

}
