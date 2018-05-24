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
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * Swarm API.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
final class RtSwarm implements Swarm {
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
     * @param baseUri Base URI, ending with /swarm.
     */
    RtSwarm(final HttpClient client, final URI baseUri) {
        this.client = client;
        this.baseUri = baseUri;
    }

    @Override
    public JsonObject inspect() throws IOException {
        return new Inspection(this.client, this.baseUri.toString());
    }

    @Override
    public String init(final String listenAddress) throws IOException {
        return this.init(
            Json.createObjectBuilder()
                .add("ListenAddr", listenAddress)
                .add("ForceNewCluster", false)
                .build()
        );
    }

    @Override
    public String init(final JsonObject spec) throws IOException {
        final HttpPost init = new HttpPost(this.baseUri.toString() + "/init");
        try {
            init.setEntity(
                new StringEntity(
                    spec.toString(), ContentType.APPLICATION_JSON
                )
            );
            return EntityUtils.toString(
                this.client.execute(
                    init,
                    new MatchStatus(init.getURI(), HttpStatus.SC_OK)
                ).getEntity()
            );
        } finally {
            init.releaseConnection();
        }
    }

    @Override
    public void leave(final boolean force) throws IOException {
        final HttpPost leave = new HttpPost(
            this.baseUri.toString() + "/leave?force=" + String.valueOf(force)
        );
        try {
            this.client.execute(
                leave,
                new MatchStatus(leave.getURI(), HttpStatus.SC_OK)
            );
        } finally {
            leave.releaseConnection();
        }
    }
}
