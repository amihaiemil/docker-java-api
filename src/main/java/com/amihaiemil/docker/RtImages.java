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
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

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
     * Ctor.
     * @param client The http client.
     * @param uri The URI for this Images API.
     */
    RtImages(final HttpClient client, final URI uri) {
        this.client = client;
        this.baseUri = uri;
    }

    @Override
    public Iterable<Image> iterate() throws IOException {
        final HttpGet get = new HttpGet(
            this.baseUri.toString().concat("/json")
        );
        try {
            final HttpResponse response = this.client.execute(
                get,
                new MatchStatus(get.getURI(), HttpStatus.SC_OK)
            );
            return Json.createReader(response.getEntity().getContent())
                .readArray()
                .stream()
                .map(json -> (JsonObject) json)
                .map(json -> new RtImage(
                    this.client,
                    URI.create(
                        this.baseUri.toString() + "/" + json.getString("Id")
                    )
                )).collect(Collectors.toList());
        } finally {
            get.releaseConnection();
        }
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public Images create(
        final String name, final URL source, final String repo, final String tag
    ) throws IOException, UnexpectedResponseException {
        final HttpPost create  = new HttpPost(
            new UncheckedUriBuilder(this.baseUri.toString().concat("/create"))
                .addParameter("fromImage", name)
                .addParameter("fromSrc", source.toString())
                .addParameter("repo", repo)
                .addParameter("tag", tag)
                .build()
        );
        try {
            this.client.execute(
                create,
                new MatchStatus(create.getURI(), HttpStatus.SC_OK)
            );
            return this;
        } finally {
            create.releaseConnection();
        }
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

    
    // @todo #84:30min Should return an Iterator<? extends JsonResource>
    //  which would take a Request, a HttpClient and a Mapper in its ctor,
    //  to know how to map each JsonObject to its resource type
    //  (Image, Containter etc) 
    @Override
    public Iterator<Image> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
