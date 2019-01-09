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
import java.io.Reader;
import java.net.URI;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

/**
 * Restful container logs.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.2
 * @todo #135:30min Continue implementing this class, take into consideration
 *  the query parameters as described here:
 *  https://docs.docker.com/engine/api/v1.35/#operation/ContainerLogs
 *  Since the class should be immutable, the parameters should come in the ctor
 *  and appended to the requests when they are performed. Let's leave this part
 *  for v0.0.3 or later, it's not urgent now.
 * @todo #258:30min Find a way to create more elegant solution for optional
 *  parameters like stdout and stderr. There are more optional parameters so,
 *  maybe use a Map instead of separate class attributes.
 */
final class RtLogs implements Logs {
    
    /**
     * Container which owns these logs.
     */
    private final Container owner;
    
    /**
     * Apache HttpClient which sends the requests.
     */
    private final HttpClient client;
    
    /**
     * Base URI.
     */
    private final URI baseUri;

    /**
     * Flag for stdout option.
     */
    private final boolean stdout;

    /**
     * Flag for stderr option.
     */
    private final boolean stderr;
    
    /**
     * Ctor.
     * @param owner Container which has these logs.
     * @param client Given HTTP Client.
     * @param baseUri Base URI of these logs.
     */
    RtLogs(final Container owner, final HttpClient client, final URI baseUri) {
        this.owner = owner;
        this.client = client;
        this.baseUri = baseUri;
        this.stdout = true;
        this.stderr = true;
    }

    /**
     * Ctor.
     * @param owner Container which has these logs.
     * @param client Given HTTP Client.
     * @param baseUri Base URI of these logs.
     * @param stdout Flag for stdout option.
     * @param stderr Flag for stderr option.
     * @checkstyle ParameterNumber (3 lines)
     */
    private RtLogs(final Container owner, final HttpClient client,
                   final URI baseUri, final boolean stdout,
                   final boolean stderr) {
        this.owner = owner;
        this.client = client;
        this.baseUri = baseUri;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    @Override
    public String fetch() throws IOException, UnexpectedResponseException {
        final HttpGet fetch = new HttpGet(
            new UncheckedUriBuilder(this.baseUri.toString())
                .addParameter("stdout", Boolean.toString(this.stdout))
                .addParameter("stderr", Boolean.toString(this.stderr))
                .build()
        );
        try {
            return this.client.execute(
                fetch,
                new ReadString(
                    new MatchStatus(
                        fetch.getURI(),
                        HttpStatus.SC_OK
                    )
                )
            );
        } finally {
            fetch.releaseConnection();
        }
    }

    @Override
    public Reader follow()
        throws IOException, UnexpectedResponseException {
        final HttpGet follow = new HttpGet(
            new UncheckedUriBuilder(this.baseUri.toString())
                .addParameter("follow", "true")
                .addParameter("stdout", Boolean.toString(this.stdout))
                .addParameter("stderr", Boolean.toString(this.stderr))
                .build()
        );
        return this.client.execute(
            follow,
            new ReadStream(
                new MatchStatus(
                    follow.getURI(),
                    HttpStatus.SC_SWITCHING_PROTOCOLS
                )
            )
        );
    }

    @Override
    public Logs stdout() throws IOException, UnexpectedResponseException {
        return new RtLogs(this.owner, this.client, this.baseUri, true, false);
    }

    @Override
    public Logs stderr() throws IOException, UnexpectedResponseException {
        return new RtLogs(this.owner, this.client, this.baseUri, false, true);
    }

    @Override
    public Container container() {
        return this.owner;
    }

    @Override
    public String toString() {
        try {
            return this.fetch();
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "IOException when fetching the logs of Container "
                + this.owner.containerId(),
                ex
            );
        }
    }
    
}
