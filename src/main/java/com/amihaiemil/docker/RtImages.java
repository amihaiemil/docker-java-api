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
import java.net.URL;
import java.util.Iterator;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import javax.json.Json;

/**
 * Runtime {@link Images}.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
final class RtImages implements Images {
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
     * @param client The http client.
     * @param uri The URI for this Images API.
     * @param dkr The docker entry point.
     */
    RtImages(final HttpClient client, final URI uri, final Docker dkr) {
        this.client = client;
        this.baseUri = uri;
        this.docker = dkr;
    }

    @Override
    public Image pull(
        final String name, final String tag
    ) throws IOException, UnexpectedResponseException {
        final HttpPost create  = new HttpPost(
            new UncheckedUriBuilder(this.baseUri.toString().concat("/create"))
                .addParameter("fromImage", name)
                .addParameter("tag", tag)
                .build()
        );
        try {
            this.client.execute(
                create,
                new MatchStatus(create.getURI(), HttpStatus.SC_OK)
            );
            return new RtImage(
                Json.createObjectBuilder().add("Name", name).build(),
                this.client,
                URI.create(
                    this.baseUri.toString() + "/" + name
                ),
                this.docker
            );
        } finally {
            create.releaseConnection();
        }
    }

    @Override
    public Image importImage(
        final URL source, final String repo
    ) throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            "Not yet implemented. If you can contribute please,"
            + " do it here: https://www.github.com/amihaiemil/docker-java-api"
        );
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
    public Iterator<Image> iterator() {
        return new ResourcesIterator<>(
            this.client,
            new HttpGet(this.baseUri.toString().concat("/json")),
            json-> new RtImage(
                json,
                this.client,
                URI.create(
                    this.baseUri.toString() + "/" + json.getString("Id")
                ),
                this.docker
            )
        );
    }

    @Override
    public Docker docker() {
        return this.docker;
    }
    
}
