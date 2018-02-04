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

import com.jcabi.http.Request;
import com.jcabi.http.Response;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * HTTP Response comming from the Unix Socket.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #14:30min Implement and test the headers() method.
 * @todo #14:30min Refine the body reading logic. Have to take into account
 *  the Transfer-Encoding header and cover the empty body case.
 */
final class SocketResponse implements Response {

    /**
     * Initial Request.
     */
    private Request request;

    /**
     * Response as String.
     */
    private String response;

    /**
     * Ctor.
     * @param request Initla HTTP request.
     * @param response HTTP Response as String.
     */
    SocketResponse(final Request request, final String response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public Request back() {
        return this.request;
    }

    @Override
    public int status() {
        final String statusLine = this.response.substring(
            0, this.response.indexOf("\n")
        );
        return Integer.valueOf(statusLine.split(" ")[1]);
    }

    @Override
    public String reason() {
        final String statusLine = this.response.substring(
            0, this.response.indexOf("\n")
        );
        return statusLine.split(" ")[2];
    }

    @Override
    public Map<String, List<String>> headers() {
        return null;
    }

    @Override
    public String body() {
        return this.response.substring(this.response.indexOf("\n\n")).trim();
    }

    @Override
    public byte[] binary() {
        return this.response.substring(
            this.response.indexOf("\n\n")
        ).getBytes();
    }

    @Override
    public <T extends Response> T as(final Class<T> type) {
        try {
            return type.getDeclaredConstructor(Response.class)
                    .newInstance(this);
        } catch (final InstantiationException
                | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
