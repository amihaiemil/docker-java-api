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

import javax.json.JsonObject;
import java.io.IOException;

/**
 * Networks API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #224:30min Extend create with options to specify driver,
 *  driver options, IPAM and add labels as specified at:
 *  https://docs.docker.com/engine/api/v1.35/#operation/NetworkCreate. Then
 *  remove Ignore annotation from RtNetworksTest.createWithParametersOk
 */
public interface Networks extends Iterable<Network> {

    /**
     * Create a network.
     * @param name Name of the network.
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (200 OK).
     * @return The created network.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/NetworkCreate">Create a network</a>
     */
    Network create(final String name)
        throws IOException, UnexpectedResponseException;

    /**
     * Create a network with additional parameters.
     * @param name Name of the network
     * @param parameters Network parameters
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (200 OK).
     * @return The created network.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/NetworkCreate">Create a network</a>
     */
    Network create(final String name, JsonObject parameters)
        throws IOException, UnexpectedResponseException;

    /**
     * Deletes unused networks.
     * @throws IOException If an I/O error occurs.
     * @throws UnexpectedResponseException If the API responds with an
     *  unexpected status.
     */
    void prune() throws IOException, UnexpectedResponseException;

    /**
     * Return the Docker engine where these Networks came from.
     * @return Docker.
     */
    Docker docker();

}
