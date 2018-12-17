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
import javax.json.JsonObject;
import org.apache.http.client.HttpClient;

/**
 * Runtime {@link Network}.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
final class RtNetwork extends JsonResource implements Network {

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
     * @param rep JsonObject representation of this Volume.
     * @param client The http client.
     * @param uri The URI for this network.
     * @param dkr The docker entry point.
     * @checkstyle ParameterNumber (5 lines)
     */
    RtNetwork(
        final JsonObject rep, final HttpClient client,
        final URI uri, final Docker dkr
    ) {
        super(rep);
        this.client = client;
        this.baseUri = uri;
        this.docker = dkr;
    }

    @Override
    public JsonObject inspect()
        throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            String.join(" ",
                "Network.inspect() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }

    @Override
    public void remove() throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            String.join(" ",
                "Network.remove() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }

    @Override
    public void connect(final String containerId)
        throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            String.join(" ",
                "Network.connect() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }

    @Override
    public void disconnect(final String containerId)
        throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            String.join(" ",
                "Network.disconnect() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }
}
