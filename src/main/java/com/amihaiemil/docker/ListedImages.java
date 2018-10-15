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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/**
 * Listed images, which may have a filter applied.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.3
 * @todo #144:30min Finish implementation here, add a Map to this class, that
 *  would hold the actual filters and apply them when making the call in the
 *  iterator() method. Also, more ctors should be available, at least one with
 *  filters and one without filters.
 */
final class ListedImages extends RtImages {
    /**
     * Image filters.
     */
    private final Map<String, Iterable<String>> filters;

    /**
     * Ctor.
     * @param client The http client.
     * @param uri The URI for this Images API.
     * @param dkr The docker entry point.
     * @checkstyle ParameterNumber (2 lines)
     */
    ListedImages(final HttpClient client, final URI uri, final Docker dkr) {
        this(client, uri, dkr, Collections.emptyMap());
    }

    /**
     * Ctor.
     * @param client The http client.
     * @param uri The URI for this Images API.
     * @param dkr The docker entry point.
     * @param filters Image filter
     * @checkstyle ParameterNumber (2 lines)
     */
    ListedImages(
        final HttpClient client, final URI uri,
        final Docker dkr, final Map<String, Iterable<String>> filters
    ) {
        super(client, uri, dkr);
        this.filters = filters;
    }

    @Override
    public Iterator<Image> iterator() {
        final UncheckedUriBuilder uri = new UncheckedUriBuilder(
            super.baseUri().toString().concat("/json")
        );
        if (!this.filters.isEmpty()) {
            final JsonObjectBuilder json = Json.createObjectBuilder();
            this.filters.forEach(
                (name, values) -> {
                    final JsonArrayBuilder array = Json.createArrayBuilder();
                    values.forEach(array::add);
                    json.add(name, array);
                }
            );
            uri.addParameter("filters", json.build().toString());
        }
        return new ResourcesIterator<>(
            super.client(),
            new HttpGet(uri.build()),
            img -> new RtImage(
                img,
                super.client(),
                URI.create(
                    super.baseUri().toString() + "/" + img.getString("Id")
                ),
                super.docker()
            )
        );
    }

    @Override
    public Images filter(final Map<String, Iterable<String>> fltrs) {
        final Map<String, Iterable<String>> merged = new HashMap<>(
            this.filters
        );
        merged.putAll(fltrs);
        return new ListedImages(
            super.client(),
            this.baseUri(),
            this.docker(),
            merged
        );
    }
}
