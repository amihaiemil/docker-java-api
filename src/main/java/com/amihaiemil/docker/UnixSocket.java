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

import com.jcabi.http.*;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;

import java.io.*;
import java.nio.channels.Channels;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A HTTP request performed over a unix socket.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #9:30min Write a test that will open a socket
 *  to which this request is sent.
 */
final class UnixSocket implements Request {

    /**
     * Base request, sent through the Socket wire.
     */
    private Request base;

    /**
     * Ctor.
     * @param path Path to the socket on disk.
     * @param uri Request URI.
     */
    UnixSocket(final String path, final String uri) {
        this.base = new BaseRequest(
            new SocketWire(path), uri
        );
    }

    @Override
    public RequestURI uri() {
        return this.base.uri();
    }

    @Override
    public RequestBody body() {
        return this.base.body();
    }

    @Override
    public RequestBody multipartBody() {
        return this.base.multipartBody();
    }

    @Override
    public Request header(final String name, final Object value) {
        return this.base.header(name, value);
    }

    @Override
    public Request reset(final String name) {
        return this.base.reset(name);
    }

    @Override
    public Request method(final String method) {
        return this.base.method(method);
    }

    @Override
    public Request timeout(final int cnct, final int rdd) {
        return this.base.timeout(cnct, rdd);
    }

    @Override
    public Response fetch() throws IOException {
        return this.base.fetch();
    }

    @Override
    public Response fetch(final InputStream stream) throws IOException {
        return this.base.fetch(stream);
    }

    @Override
    public <T extends Wire> Request through(
        final Class<T> type, final Object... args
    ) {
        return this.through(type, args);
    }

    /**
     * Wire through which this request is sent.
     * @checkstyle ParameterNumber (20 lines).
     */
    private static final class SocketWire implements Wire {

        /**
         * Path to the socket.
         */
        private String path;

        /**
         * Ctor.
         * @param path Path to the socket on disk.
         */
        SocketWire(final String path) {
            this.path = path;
        }

        @Override
        public Response send(
            final Request req, final String home, final String method,
            final Collection<Map.Entry<String, String>> headers,
            final InputStream content, final int connect, final int read
        ) throws IOException {
            final StringBuilder hdrs = new StringBuilder();
            for(final Map.Entry<String, String> header : headers) {
                hdrs.append(header.getKey() + ": " + header.getValue())
                    .append("\n");
            }

            final String request = String.format(
                this.template(), method, home,
                hdrs.toString().trim(), this.readContent(content)
            );

            try (
                final UnixSocketChannel channel = UnixSocketChannel.open(
                    new UnixSocketAddress(this.path)
                );
                final OutputStream client = Channels.newOutputStream(channel);
                final InputStream response = Channels.newInputStream(channel)
            ) {
                client.write(request.getBytes());
                return new SocketResponse(req, this.readContent(response));
            }
        }

        /**
         * HTTP request template.
         * @return String.
         */
        private String template() {
            final StringBuilder message = new StringBuilder();
            message
                .append("%s %s HTTP/1.1\r\n")
                .append("%s")
                .append("\r\n").append("\r\n")
                .append("%s")
                .append("\r\n");
            return message.toString().trim();
        }


        /**
         * Read the content InputStream into a String.
         * @param input Content stream.
         * @return String.
         * @throws IOException If something goes wrong.
         */
        public static String readContent(
            final InputStream input
        ) throws IOException {
            try (
                final BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(input)
                )
            ) {
                return buffer.lines().collect(Collectors.joining("\n"));
            }
        }

    }
}
