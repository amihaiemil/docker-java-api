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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * A HTTP request performed over a unix socket.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
final class UnixSocket implements Request {

    /**
     * Base request, sent through the Socket wire.
     */
    private Request base;

    /**
     * Ctor.
     * @param path Path to the socket on disk.
     */
    UnixSocket(final String path) {
        this.base = new BaseRequest(new SocketWire(), path);
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
     * @checkstyle ParameterNumber (20 lines)
     * @todo #7:30min Implement this method, use UnixSocketAddress to send the
     *  request via the unix socket.
     */
    private static final class SocketWire implements Wire {

        @Override
        public Response send(
            final Request req,
            final String home,
            final String method,
            final Collection<Map.Entry<String, String>> headers,
            final InputStream content,
            final int connect,
            final int read
        ) throws IOException {
            return null;
        }
    }
}
