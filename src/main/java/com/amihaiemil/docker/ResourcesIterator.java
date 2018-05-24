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

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Iterator over Docker resources (Containers, Images etc).
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @param <T> The Json resoure (Image, Container etc) returned by the API.
 */
final class ResourcesIterator<T extends JsonObject> implements Iterator<T> {

    /**
     * Iterated resources.
     */
    private final Iterator<T> resources;

    /**
     * Ctor.
     * @param client Used HTTP Client.
     * @param request HTTP Request.
     * @param mapper Function which should map the received JsonObject
     *  to the specified resource.
     */
    ResourcesIterator(
        final HttpClient client, final HttpGet request,
        final Function<JsonObject, T> mapper
    ) {
        try {
            final JsonArray array = client.execute(
                request,
                new ReadJsonArray(
                    new MatchStatus(request.getURI(), HttpStatus.SC_OK)
                )
            );
            this.resources = array.stream()
                .map(json -> (JsonObject) json)
                .map(
                    json -> mapper.apply(json)
                ).collect(Collectors.toList())
                .iterator();
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "IOException when calling " + request.getURI().toString(), ex
            );
        } finally {
            request.releaseConnection();
        }
    }

    @Override
    public boolean hasNext() {
        return this.resources.hasNext();
    }

    @Override
    public T next() {
        return this.resources.next();
    }
}
