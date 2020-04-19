/**
 * Copyright (c) 2018-2020, Mihai Emil Andronache
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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Events API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
 */
public interface Events {

    /**
     * Show events created since the specified timestamp,
     * then stream new events.
     * @param timestamp Given timestamp.
     * @return Filtered Events.
     */
    Events since(final LocalDateTime timestamp);

    /**
     * Show events created until the specified timestamp,
     * then stop streaming.
     * @param timestamp Given timestamp.
     * @return Filtered Events.
     */
    Events until(final LocalDateTime timestamp);

    /**
     * Filter these Events.
     * @param filter Supplier of filters.
     * @return Filtered Events.
     * @see <a href="https://docs.docker.com/engine/api/v1.40/#operation/SystemEvents">Docker API Docs</a>
     */
    Events filter(final Supplier<Map<String, Iterable<String>>> filter);

    /**
     * Start monitoring these events. Pay attention:<br><br>
     * The Stream is <b>infinite</b>, which means you have to specify a
     * <b>limit</b> before calling a terminal operation on it. Otherwise,
     * your terminal operation will run until the Server closes the connection.
     * Example:
     * <pre>
     *   final Docker docker = ...;
     *   final List&lt;JsonObject&gt; firstTen = docker.events().monitor()
     *       .limit(10).collect(Collectors.toList());
     *   //It will wait for and return the first 10 real-time events
     *   //from the server.
     * </pre>
     * @throws IOException If there is any I/O problem.
     * @throws UnexpectedResponseException If the response is not 200 OK.
     * @return Stream of events.
     */
    Stream<JsonObject> monitor()
        throws IOException, UnexpectedResponseException;

    /**
     * Docker where these events came from.
     * @return Docker.
     */
    Docker docker();
}
