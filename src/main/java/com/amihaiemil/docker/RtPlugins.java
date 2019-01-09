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

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import javax.json.JsonArray;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
    public void create(final String name, final String directory)
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
                new StringEntity(directory)
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
                               final JsonArray properties)
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
                new StringEntity(properties.toString())
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

    @Override
    public Iterator<PluginPrivilege> privileges(final String remote)
        throws IOException, UnexpectedResponseException {
        final UncheckedUriBuilder uri =
            new UncheckedUriBuilder(
                this.baseUri.toString().concat("/privileges")
            ).addParameter("remote", remote);

        return new ResourcesIterator<>(
            this.client,
            new HttpGet(
                uri.build()
            ),
            PluginPrivilege::new
        );
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
