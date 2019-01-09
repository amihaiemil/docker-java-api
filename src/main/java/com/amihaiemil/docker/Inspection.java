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
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

/**
 * An inpsection upon any of the Docker resources.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
final class Inspection extends JsonResource {

    /**
     * Ctor.
     * @param client The Http client.
     * @param url The request URL.
     * @throws UnexpectedResponseException If Docker's response code is not 200.
     * @throws IOException If an I/O error occurs.
     */
    Inspection(final HttpClient client, final String url)
        throws UnexpectedResponseException, IOException {
        super(fetch(client, url));
    }
    
    /**
     * Fetch the JsonObject resource.
     * @param client The Http client.
     * @param url The request URL.
     * @return The fetched JsonObject.
     * @throws UnexpectedResponseException If Docker's response code is not 200.
     * @throws IOException If an I/O error occurs.
     */
    private static JsonObject fetch(final HttpClient client, final String url)
        throws UnexpectedResponseException, IOException {
        final HttpGet inspect = new HttpGet(url);
        try {
            return client.execute(
                inspect,
                new ReadJsonObject(
                    new MatchStatus(inspect.getURI(), HttpStatus.SC_OK)
                )
            );
        } finally {
            inspect.releaseConnection();
        }
    }
}
