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

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;

/**
 * Runtime {@link Version}.
 * @author Michael Lux (michi.lux@gmail.com)
 * @since 0.0.11
 */
final class RtVersion extends JsonResource implements Version {
    /**
     * Ctor.
     * @param client The http client.
     * @param uri The URI for this version.
     * @throws IOException If an I/O error occurs.
     */
    RtVersion(final HttpClient client, final URI uri) throws IOException {
        super(fetch(client, uri));
    }

    /**
     * Fetch the JsonObject resource.
     * @param client The Http client.
     * @param uri The request URL.
     * @return The fetched JsonObject.
     * @throws UnexpectedResponseException If Docker's response code is not 200.
     * @throws IOException If an I/O error occurs.
     */
    private static JsonObject fetch(final HttpClient client, final URI uri)
        throws UnexpectedResponseException, IOException {
        final HttpGet version = new HttpGet(uri);
        try {
            return client.execute(
                version,
                new ReadJsonObject(
                    new MatchStatus(version.getURI(), HttpStatus.SC_OK)
                )
            );
        } finally {
            version.releaseConnection();
        }
    }

    /**
     * Returns the version of the connected docker engine.
     * @return Version of connected docker engine
     */
    public String getVersion() {
        return this.getString("Version");
    }

    /**
     * Returns the name of the connected docker platform.
     * @return Name of the docker platform
     */
    public String getPlatformName() {
        return this.getJsonObject("Platform").getString("Name");
    }

    /**
     * Returns the API version of the docker engine.
     * @return API version
     */
    public String getApiVersion() {
        return this.getString("ApiVersion");
    }

    /**
     * Returns the minimum API version of the docker engine.
     * @return Minimum API version
     */
    public String getMinApiVersion() {
        return this.getString("MinAPIVersion");
    }

    /**
     * Returns the OS docker is running on.
     * @return Name of the OS docker is running on
     */
    public String getOs() {
        return this.getString("Os");
    }

    /**
     * Returns the (CPU) architecture docker is running on.
     * @return The (CPU) architecture docker is running on
     */
    public String getArch() {
        return this.getString("Arch");
    }

    /**
     * Reports whether experimental docker features are enabled.
     * @return Whether experimental docker features are enabled
     */
    public boolean isExperimental() {
        return this.getBoolean("Experimental");
    }
}
