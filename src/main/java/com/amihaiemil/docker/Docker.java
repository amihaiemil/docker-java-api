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
import java.io.Reader;
import java.util.stream.Stream;

import org.apache.http.client.HttpClient;

import javax.json.JsonObject;

/**
 * Docker API entry point.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Docker {

    /**
     * Ping the Docker Engine.
     * @return True if it responds with 200 OK, false otherwise.
     * @throws IOException If there's network problem.
     */
    boolean ping() throws IOException;

    /**
     * Read events from the server in real time. Pay attention:<br><br>
     * The Stream is <b>infinite</b>, which means you have to specify a
     * <b>limit</b> before calling a terminal operation on it. Otherwise,
     * your terminal operation will run until the Server closes the connection.
     * Example:
     * <pre>
     *   final Docker docker = ...;
     *   final List&lt;JsonObject&gt; firstTen = docker.events()
     *       .limit(10).collect(Collectors.toList());
     *   //It will wait for and return the first 10 real-time events
     *   //from the server.
     * </pre>
     * @return The events as a {@link Stream}.
     * @throws IOException If an I/O error occurs.
     * @throws UnexpectedResponseException If the API responds with an
     *  unexpected status.
     */
    Stream<JsonObject> events()
        throws IOException, UnexpectedResponseException;

    /**
     * Entry point for the Containers API.
     * @return Containers.
     */
    Containers containers();

    /**
     * Entry point for the Images API.
     * @return Images.
     */
    Images images();

    /**
     * Entry point for the Networks API.
     * @return Networks.
     */
    Networks networks();

    /**
     * Entry point for the Volumes API.
     * @return Volumes.
     */
    Volumes volumes();

    /**
     * Entry point for the Exec API.
     * @return Execs.
     */
    Execs execs();

    /**
     * Entry point for the Swarm API.
     * @return Swarm.
     */
    Swarm swarm();

    /**
     * Entry point for the DockerSystem API.
     * @return DockerSystem.
     */
    DockerSystem system();

    /**
     * Entry point for the Plugins API.
     * @return Plugins.
     */
    Plugins plugins();

    /**
     * Entry point for Version API.
     * @return Version.
     * @throws IOException If an I/O error occurs.
     */
    Version version() throws IOException;

    /**
     * The underlying, immutable, Apache HttpClient.<br><br>
     *
     * Use this method to fetch the underlying HttpClient and perform your own
     * HTTP requests, in case the API is missing the desired method.<br><br>
     *
     * Usage in any other scenario is discouraged. Try to find the desired
     * method before using this. Also, if the method is missing, open an
     * Issue at https://www.github.com/amihaiemil/docker-java-api, maybe
     * it can be implemented and released quickly.
     *
     * @return The underlying HttpClient.
     */
    HttpClient httpClient();
}
