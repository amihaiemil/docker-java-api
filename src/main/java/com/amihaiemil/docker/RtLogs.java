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
 * @todo #130:30min Continue implementing this class (method fetch) and also
 *  take into consideration the query parameters as described here:
 *  https://docs.docker.com/engine/api/v1.35/#operation/ContainerLogs
 * @todo #130:30min Write some ITCase for the fetch() method. We might have to
 *  implement the stream decoding (in case TTY is disabled when the Container
 *  is created), as explained here, in "Stream format" paragraph:
 *  https://docs.docker.com/engine/api/v1.37/#operation/ContainerAttach
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
     * Ctor.
     * @param owner Container which has these logs.
     * @param client Given HTTP Client.
     * @param baseUri Base URI of these logs.
     */
    RtLogs(final Container owner, final HttpClient client, final URI baseUri) {
        this.owner = owner;
        this.client = client;
        this.baseUri = baseUri;
    }

    @Override
    public String fetch() throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            "Operation not yet implemented. If you can contribute please,"
            + " do it here: https://www.github.com/amihaiemil/docker-java-api"
        );
    }

    @Override
    public Reader follow()
        throws IOException, UnexpectedResponseException {
        final HttpGet follow = new HttpGet(
            new UncheckedUriBuilder(this.baseUri.toString())
                .addParameter("follow", "true")
                .build()
        );
        return this.client.execute(
            follow,
            new ReadLogsStream(
                new MatchStatus(
                    follow.getURI(),
                    HttpStatus.SC_SWITCHING_PROTOCOLS
                )
            )
        );
    }

    @Override
    public Container container() {
        return this.owner;
    }

}
