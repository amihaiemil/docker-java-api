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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.apache.http.client.utils.URIBuilder;

/**
 * A {@link URIBuilder} that hides checked exceptions in the methods used
 * throughout this library. Used under the assumption that the structure
 * of URIs created using this class are valid.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
final class UncheckedUriBuilder extends URIBuilder {
    /**
     * Ctor.
     * @param uri Base URI.
     * @throws IllegalArgumentException From {@link URI#create(String)}.
     * @throws NullPointerException From {@link URI#create(String)}.
     */
    UncheckedUriBuilder(
        final String uri
    ) throws IllegalArgumentException, NullPointerException {
        super(URI.create(uri));
    }

    @Override
    public UncheckedUriBuilder addParameter(
        final String name, final String value
    ) {
        super.addParameter(name, value);
        return this;
    }

    /**
     * Adds a JSON encoded `filters` parameter.
     * 
     * @param filters Value of the filters.
     * @return A {@link UncheckedUriBuilder} instance.
     * 
     * @todo #227:30min we should take out this functionality in a URIBuilder 
     * decorator. It should be used like this:
     * <pre>
     *     final URIBuilder buidler = new Filtered(
     *          new UncheckedUriBuilder(...), filters
     *     );
     * </pre>
     */
    public UncheckedUriBuilder addFilters(
        final Map<String, Iterable<String>> filters
    ) {
        if (filters != null && !filters.isEmpty()) {
            final JsonObjectBuilder json = Json.createObjectBuilder();
            filters.forEach(
                (name, values) -> {
                    final JsonArrayBuilder array = Json.createArrayBuilder();
                    values.forEach(array::add);
                    json.add(name, array);
                }
            );
            this.addParameter("filters", json.build().toString());
        }
        
        return this;
    }
    
    @Override
    public URI build() {
        try {
            return super.build();
        } catch (final URISyntaxException ex) {
            throw new IllegalStateException(
                "Unexpected error while building a URI!", ex
            );
        }
    }
}
