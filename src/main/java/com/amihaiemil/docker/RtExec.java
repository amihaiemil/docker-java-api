/**
 * Copyright (c) 2018-2020, Mihai Emil Andronache
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

import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

/**
 * Exec. A batch of commands that are running inside a Container.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.12
 */
final class RtExec implements Exec {
    /**
     * Apache HttpClient which sends the requests.
     */
    private final HttpClient client;

    /**
     * URI of this Exec.
     */
    private final URI baseUri;

    /**
     * Docker API.
     */
    private final Docker docker;

    /**
     * Ctor.
     * @param client HTTP Client used to send the requests.
     * @param uri The URI for this Images API.
     * @param dkr The docker entry point.
     * @checkstyle ParameterNumber (10 lines)
     */
    RtExec(final HttpClient client, final URI uri, final Docker dkr) {
        this.client = client;
        this.baseUri = uri;
        this.docker = dkr;
    }

    @Override
    public JsonObject inspect()
        throws IOException, UnexpectedResponseException {
        return new Inspection(this.client, this.baseUri.toString() + "/json");
    }

    /**
     * Return low-level information about this exec instance.
     *
     * @param containerId
     * @return ExecInstance object.
     * @throws IOException                 If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *                                     the expected one (200 OK).
     */
    @Override
    public ExecInstance create(final String containerId, final JsonObject exec) throws IOException, UnexpectedResponseException {
        final URI uri = new UncheckedUriBuilder(
            this.baseUri.toString().concat("/containers/" + containerId).concat("/exec")
        ).build();
        final HttpPost post = new HttpPost(uri);
        try {
            post.setEntity(new StringEntity(exec.toString()));
            post.setHeader(new BasicHeader("Content-Type", "application/json"));
            final JsonObject json = this.client.execute(
                post,
                new ReadJsonObject(
                    new MatchStatus(post.getURI(), HttpStatus.SC_CREATED)
                )
            );
            return new RtExecInstance(this.client,
                URI.create(
                    this.baseUri.toString() + "/exec/" + json.getString("Id")
                ),
                this.docker);
        } finally {
            post.releaseConnection();
        }
    }

}
