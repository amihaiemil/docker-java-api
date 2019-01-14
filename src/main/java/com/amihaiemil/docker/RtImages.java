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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringJoiner;
import javax.json.Json;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * Runtime {@link Images}.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
abstract class RtImages implements Images {
    /**
     * Apache HttpClient which sends the requests.
     */
    private final HttpClient client;

    /**
     * Base URI for Images API.
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
     * @checkstyle ParameterNumber (10 lines)
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
        final HttpPost create  = new HttpPost(
            new UncheckedUriBuilder(this.baseUri.toString().concat("/create"))
                .addParameter("fromSrc", source.toString())
                .addParameter("repo", repo)
                .build()
        );
        try {
            this.client.execute(
                create,
                new MatchStatus(create.getURI(), HttpStatus.SC_OK)
            );
            return new RtImage(
                Json.createObjectBuilder().add("Name", repo).build(),
                this.client,
                URI.create(
                    String.format("%s/%s", this.baseUri.toString(), repo)
                ),
                this.docker
            );
        } finally {
            create.releaseConnection();
        }
    }

    @Override
    public Images importFromTar(
        final String file) throws IOException, UnexpectedResponseException {
        final HttpPost load  = new HttpPost(
            new UncheckedUriBuilder(this.baseUri.toString().concat("/load"))
                .build()
        );
        try {
            load.setEntity(
                new StringEntity(
                    new String(
                        Files.readAllBytes(Paths.get(file))
                    ),
                    ContentType.DEFAULT_BINARY
                )
            );
            this.client.execute(
                load,
                new MatchStatus(load.getURI(), HttpStatus.SC_OK)
            );
        } finally {
            load.releaseConnection();
        }
        return this;
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
    public Reader save() throws IOException, UnexpectedResponseException {
        final Reader tarball;
        final StringJoiner names = new StringJoiner(",");
        for(final Image img : this) {
            names.add(img.getString("Id"));
        }
        if(names.toString().isEmpty()) {
            tarball = new InputStreamReader(
                new ByteArrayInputStream(new byte[]{})
            );
        } else {
            final HttpGet save = new HttpGet(
                new UncheckedUriBuilder(this.baseUri.toString().concat("/get"))
                    .addParameter("names", names.toString())
                    .build()
            );
            tarball = this.client.execute(
                save,
                new ReadStream(
                    new MatchStatus(save.getURI(), HttpStatus.SC_OK)
                )
            );
        }
        return tarball;
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
}
