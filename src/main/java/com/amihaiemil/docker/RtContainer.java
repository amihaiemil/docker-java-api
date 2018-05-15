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

import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;

/**
 * Restful Container.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #97:30min Continue implementing the rest of the Container operations
 *  (pause, unpause, logs etc) See the Docker API Docs for reference:
 *  https://docs.docker.com/engine/api/v1.35/#tag/Container
 * @todo #58:30min Now that we have the CI environment properly setup with
 *  a Docker instance, continue integration tests for RtContainer(s) and other
 *  parts of the API.
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
        return new Inspection(this.client, this.baseUri.toString() + "/json");
    }

    @Override
    public void start() throws IOException {
        final HttpPost start = new HttpPost(
            this.baseUri.toString() + "/start"
        );
        final HttpResponse response = this.client.execute(start);
        final int status = response.getStatusLine().getStatusCode();
        if(status != HttpStatus.SC_NO_CONTENT) {
            throw new UnexpectedResponseException(
                start.getURI().toString(), status, HttpStatus.SC_NO_CONTENT
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

    @Override
    public void stop() throws IOException {
        final HttpPost stop = new HttpPost(
            this.baseUri.toString() + "/stop"
        );
        try {
            final int status = this.client.execute(stop)
                .getStatusLine()
                .getStatusCode();
            if (status != HttpStatus.SC_NO_CONTENT) {
                throw new UnexpectedResponseException(
                    stop.getURI().toString(), status, HttpStatus.SC_NO_CONTENT
                );
            }
        } finally {
            stop.releaseConnection();
        }
    }

    @Override
    public void kill() throws IOException, UnexpectedResponseException {
        final HttpPost kill = new HttpPost(
            this.baseUri.toString() + "/kill"
        );
        try {
            final int status = this.client.execute(kill)
                .getStatusLine()
                .getStatusCode();
            if (status != HttpStatus.SC_NO_CONTENT) {
                throw new UnexpectedResponseException(
                    kill.getURI().toString(), status, HttpStatus.SC_NO_CONTENT
                );
            }
        } finally {
            kill.releaseConnection();
        }
    }

    @Override
    public void restart() throws IOException, UnexpectedResponseException {
        final HttpPost restart = new HttpPost(
            this.baseUri.toString() + "/restart"
        );
        try {
            final int status = this.client.execute(restart)
                .getStatusLine()
                .getStatusCode();
            if (status != HttpStatus.SC_NO_CONTENT) {
                throw new UnexpectedResponseException(
                    restart.getURI().toString(),
                    status, HttpStatus.SC_NO_CONTENT
                );
            }
        } finally {
            restart.releaseConnection();
        }
    }

    @Override
    public void rename(final String name)
        throws IOException, UnexpectedResponseException {
        final HttpPost rename = new HttpPost(
            this.baseUri.toString() + "/rename?name=" + name
        );
        try {
            final int status = this.client.execute(rename)
                .getStatusLine()
                .getStatusCode();
            if (status != HttpStatus.SC_NO_CONTENT) {
                throw new UnexpectedResponseException(
                    rename.getURI().toString(), status, HttpStatus.SC_NO_CONTENT
                );
            }
        } finally {
            rename.releaseConnection();
        }
    }
}
