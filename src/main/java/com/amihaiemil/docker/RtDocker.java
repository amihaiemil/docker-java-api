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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Restful Docker.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
abstract class RtDocker implements Docker {

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
     * @param baseUri Base URI.
     */
    RtDocker(final HttpClient client, final URI baseUri) {
        this.client = client;
        this.baseUri = baseUri;
    }
    
    @Override
    public final boolean ping() throws IOException {
        final HttpGet ping = new HttpGet(this.baseUri.toString() + "/_ping");
        final HttpResponse response = this.client.execute(ping);
        for(final Header header : response.getAllHeaders()) {
            System.out.println(header.getName()+": " +header.getValue());
        }
        ping.releaseConnection();
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    /**
     * Unlike other methods, we cannot implement this one using Response
     * Handlers, because Apache HTTP Client tries to consume the remaining
     * content after all the handlers have been executed, which results in
     * a blockage, since the underlying InputStream is potentially infinite.
     *
     * @return Stream of Events.
     * @throws IOException If any I/O problem occurs.
     * @throws UnexpectedResponseException If the response status is not 200.
     */
    @Override
    public Stream<JsonObject> events()
        throws IOException, UnexpectedResponseException {
        final HttpGet monitor = new HttpGet(
            this.baseUri.toString() + "/events"
        );
        final HttpResponse response = this.client.execute(monitor);
        final int actual = response.getStatusLine().getStatusCode();
        if(actual != HttpStatus.SC_OK) {
            throw new UnexpectedResponseException(
                this.baseUri.toString() + "/events",
                actual, HttpStatus.SC_OK,
                Json.createObjectBuilder().build()
            );
        } else {
            final InputStream is = response.getEntity().getContent();
            final Stream<JsonObject> stream = Stream.generate(
                () -> {
                    try {
                        final byte[] tmp = new byte[4096];
                        while ((is.read(tmp) != -1)) {
                            try {
                                final JsonReader reader = Json.createReader(
                                    new ByteArrayInputStream(tmp)
                                );
                                return reader.readObject();
                            } catch (final Exception rx) {
                                //Couldn't parse byte[] to Json,
                                //try to read more bytes.
                            }
                        }
                    } catch (final IOException ex) {
                        throw new IllegalStateException(
                            "IOException when reading streamed JsonObjects!"
                        );
                    }
                    return null;
                }
            ).onClose(
                () -> {
                    try {
                        ((CloseableHttpResponse) response).close();
                    } catch (final IOException ex) {
                        //There is a bug in Apache HTTPClient, when closing
                        //an infinite InputStream: IOException is thrown
                        //because the client still tries to read from the
                        // closed Stream. We should ignore this case.
                    }
                }
            );
            return stream;
        }
    }

    @Override
    public final Containers containers() {
        return new ListedContainers(
            this.client,
            URI.create(this.baseUri.toString() + "/containers"),
            this
        );
    }

    @Override
    public final Images images() {
        return new ListedImages(
            this.client,
            URI.create(this.baseUri.toString() + "/images"),
            this
        );
    }

    @Override
    public final Networks networks() {
        return new ListedNetworks(
            this.client,
            URI.create(this.baseUri.toString() + "/networks"),
            this
        );
    }

    @Override
    public final Volumes volumes() {
        return new ListedVolumes(
            this.client,
            URI.create(this.baseUri.toString() + "/volumes"),
            this
        );
    }

    @Override
    public final Execs execs() {
        return new RtExecs(
            this.client,
            URI.create(this.baseUri.toString() + "/exec"),
            this
        );
    }

    @Override
    public final Swarm swarm() {
        return new RtSwarm(
            this.client,
            URI.create(this.baseUri.toString().concat("/swarm")), 
            this
        );
    }

    @Override
    public DockerSystem system() {
        return new RtDockerSystem(
            this.client,
            URI.create(this.baseUri.toString().concat("/system")),
            this
        );
    }


    @Override
    public Plugins plugins() {
        throw new UnsupportedOperationException(
            String.join(" ",
                "Plugins API is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }

    @Override
    public Version version() throws IOException {
        final String versionUri = this.baseUri.toString() + "/version";
        return new RtVersion(
            this.client,
            URI.create(versionUri)
        );
    }

    @Override
    public HttpClient httpClient() {
        return this.client;
    }
}
