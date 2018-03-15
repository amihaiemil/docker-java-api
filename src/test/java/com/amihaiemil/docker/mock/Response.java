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
package com.amihaiemil.docker.mock;

import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;

/**
 * An {@link HttpResponse} suitable for tests. Can be configured with 
 * predetermined {@link HttpStatus http status code} and JSON payload.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class Response implements HttpResponse {
    /**
     * This response's status line.
     */
    private final StatusLine statusLine;
    /**
     * This response's payload.
     */
    private final HttpEntity payload;

    /**
     * Ctor.
     *
     * @param status The {@link HttpStatus http status code}
     * @param jsonPayload The json payload
     */
    public Response(final int status, final String jsonPayload) {
        this.statusLine = new BasicStatusLine(
            new ProtocolVersion("HTTP", 1, 1), status, ""
        );
        this.payload = new StringEntity(
            jsonPayload, ContentType.APPLICATION_JSON
        );
    }

    @Override
    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    @Override
    public void setStatusLine(final StatusLine statusline) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStatusLine(final ProtocolVersion ver, final int code) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStatusLine(final ProtocolVersion ver, final int code,
        final String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStatusCode(final int code) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setReasonPhrase(final String reason)
        throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpEntity getEntity() {
        return this.payload;
    }

    @Override
    public void setEntity(final HttpEntity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLocale(final Locale loc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsHeader(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Header[] getHeaders(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Header getFirstHeader(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Header getLastHeader(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Header[] getAllHeaders() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addHeader(final Header header) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addHeader(final String name, final String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHeader(final Header header) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHeader(final String name, final String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHeaders(final Header[] headers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeHeader(final Header header) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeHeaders(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HeaderIterator headerIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HeaderIterator headerIterator(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpParams getParams() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setParams(final HttpParams params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
