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
 * @todo #46:30min This class represents a Container. It has to implement the
 *  API's methods which are acting upon a docker Container like logs,
 *  delete, stop etc).
 * @todo #46:30min Once we have the CI environment properly setup with a Docker
 *  instance, write integration tests for this class as well
 *  (RtContainerITCase).
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
}