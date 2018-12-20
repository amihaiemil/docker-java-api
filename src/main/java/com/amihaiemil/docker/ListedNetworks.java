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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.HttpClient;

/**
 * Listed networks.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 * @todo #226:30min Implement ListedNetworks iterator with filtering using Map.
 *  Then remove Ignore annotations from ListedNetworkTestCase methods.
 *  See ListedImages or ListedVolumes as examples.
 */
final class ListedNetworks extends RtNetworks {

    /**
     * Ctor.
     *
     * @param client The http client.
     * @param uri The URI for this Network API.
     * @param dkr The docker entry point.
     */
    ListedNetworks(final HttpClient client, final URI uri, final Docker dkr) {
        this(client, uri, dkr, new HashMap<>());
    }

    /**
     * Ctor with filters.
     *
     * @param client The http client.
     * @param uri The URI for this Network API.
     * @param dkr The docker entry point.
     * @param filters The filters to be applied.
     * @checkstyle ParameterNumber (10 lines)
     */
    ListedNetworks(final HttpClient client, final URI uri, final Docker dkr,
        final Map<String, Iterable<String>> filters) {
        super(client, uri, dkr);
    }

    @Override
    public Iterator<Network> iterator() {
        throw new UnsupportedOperationException(
            String.join(" ",
                "ListedNetworks.iterator() is not yet implemented.",
                "If you can contribute please",
                "do it here: https://www.github.com/amihaiemil/docker-java-api"
            )
        );
    }
}
