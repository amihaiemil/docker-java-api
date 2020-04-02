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
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;

/**
 * Restful Container.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #97:30min Continue implementing the rest of the Container operations
 *  (pause, unpause, logs etc) See the Docker API Docs for reference:
 *  https://docs.docker.com/engine/api/v1.35/#tag/Container
 */
final class RtContainer extends JsonResource implements Container {

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
     * @param rep JsonObject representation of this Container.
     * @param client Given HTTP Client.
     * @param baseUri Base URI, ending with /{containerId}.
     * @param dkr Docker where this Container came from.
     * @checkstyle ParameterNumber (5 lines)
     */
    RtContainer(
        final JsonObject rep, final HttpClient client,
        final URI baseUri, final Docker dkr
    ) {
        super(rep);
        this.client = client;
        this.baseUri = baseUri;
        this.docker = dkr;
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
        try {
            this.client.execute(
                start,
                new MatchStatus(start.getURI(), HttpStatus.SC_NO_CONTENT)
            );
        } finally {
            start.releaseConnection();
        }
    }

    @Override
    public String containerId() {
        return this.getString("Id");
    }

    @Override
    public void stop() throws IOException {
        final HttpPost stop = new HttpPost(
            this.baseUri.toString() + "/stop"
        );
        try {
            this.client.execute(
                stop,
                new MatchStatus(stop.getURI(), HttpStatus.SC_NO_CONTENT)
            );
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
            this.client.execute(
                kill,
                new MatchStatus(kill.getURI(), HttpStatus.SC_NO_CONTENT)
            );
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
            this.client.execute(
                restart,
                new MatchStatus(restart.getURI(), HttpStatus.SC_NO_CONTENT)
            );
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
            this.client.execute(
                rename,
                new MatchStatus(rename.getURI(), HttpStatus.SC_NO_CONTENT)
            );
        } finally {
            rename.releaseConnection();
        }
    }

    @Override
    public void remove() throws IOException, UnexpectedResponseException {
        this.remove(false, false, false);
    }

    @Override
    public void remove(
        final boolean volumes, final boolean force, final boolean link
    ) throws IOException, UnexpectedResponseException {
        final HttpDelete remove  = new HttpDelete(
            new UncheckedUriBuilder(this.baseUri.toString())
                .addParameter("v", String.valueOf(volumes))
                .addParameter("force", String.valueOf(force))
                .addParameter("link", String.valueOf(link))
                .build()
        );
        try {
            this.client.execute(
                remove,
                new MatchStatus(remove.getURI(), HttpStatus.SC_NO_CONTENT)
            );
        } finally {
            remove.releaseConnection();
        }
    }

    @Override
    public Logs logs() {
        return new RtLogs(
            this, this.client, URI.create(this.baseUri.toString() + "/logs")
        );
    }

    @Override
    public Docker docker() {
        return this.docker;
    }

    @Override
    public void pause() throws IOException {
        final HttpPost pause = new HttpPost(
                this.baseUri.toString() + "/pause"
        );
        try {
            this.client.execute(
                    pause,
                    new MatchStatus(pause.getURI(), HttpStatus.SC_NO_CONTENT)
            );
        } finally {
            pause.releaseConnection();
        }
    }

    @Override
    public void unpause() throws IOException {
        final HttpPost unpause = new HttpPost(
                this.baseUri.toString() + "/unpause"
        );
        try {
            this.client.execute(
                    unpause,
                    new MatchStatus(unpause.getURI(), HttpStatus.SC_NO_CONTENT)
            );
        } finally {
            unpause.releaseConnection();
        }
    }

    @Override
    public void waitOn(final String state) throws IOException {
        String end = "";
        if(null == state || state.isEmpty()){
            end = String.format("?condition=%s", state);
        }
        final HttpPost waiter = new HttpPost(
            this.baseUri.toString() + "/wait" + end
        );
        try {
            this.client.execute(
                waiter,
                new MatchStatus(waiter.getURI(), HttpStatus.SC_OK)
            );
        } finally {
            waiter.releaseConnection();
        }
    }
}
