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
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.apache.http.client.HttpClient;

/**
 * Runtime {@link Plugin}.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.8
 */
final class RtPlugin extends JsonResource implements Plugin {

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
     * @param uri The URI for this image.
     * @param dkr The docker entry point.
     * @checkstyle ParameterNumber (5 lines)
     */
    RtPlugin(
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
        return new Inspection(this.client,
            String.format("%s/%s", this.baseUri.toString(), "json"));
    }

    @Override
    public void enable() throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            String.join(" ",
                "RtPlugin.enable() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }

    @Override
    public void disable() throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            String.join(" ",
                "RtPlugin.disable() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }

    @Override
    public void upgrade(final String remote, final JsonArray properties)
        throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            String.join(" ",
                "RtPlugin.upgrade() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }

    @Override
    public void push() throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            String.join(" ",
                "RtPlugin.push() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }

    @Override
    public void configure(final Map<String, String> options)
        throws IOException, UnexpectedResponseException {
        throw new UnsupportedOperationException(
            String.join(" ",
                "RtPlugin.configure() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }
}
