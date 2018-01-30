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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.ImmutableHeader;
import com.jcabi.http.Request;
import com.jcabi.http.RequestBody;
import com.jcabi.http.RequestURI;
import com.jcabi.http.Response;
import com.jcabi.http.Wire;
import com.jcabi.immutable.Array;
import com.jcabi.log.Logger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonStructure;
import javax.ws.rs.core.UriBuilder;

/**
 * THIS CLASS IS TEMPORARILY COPIED UNTIL jcabi-http 1.17.3 is released.
 * SEE HERE: https://github.com/jcabi/jcabi-http/issues/170
 *
 * Base implementation of {@link Request}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 * @checkstyle AbbreviationAsWordInName (500 lines)
 * @see Request
 * @see Response
 */
@Immutable
@Loggable(Loggable.DEBUG)
final class BaseRequest implements Request {

    /**
     * The encoding to use.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * The Charset to use.
     * @checkstyle ConstantUsageCheck (3 lines)
     */
    private static final Charset CHARSET =
            Charset.forName(BaseRequest.ENCODING);

    /**
     * An empty immutable {@code byte} array.
     */
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * Wire to use.
     */
    private final transient Wire wire;

    /**
     * Request URI.
     */
    private final transient String home;

    /**
     * Method to use.
     */
    private final transient String mtd;

    /**
     * Socket timeout to use.
     */
    private final transient int connect;

    /**
     * Read timeout to use.
     */
    private final transient int read;

    /**
     * Headers.
     */
    private final transient Array<Map.Entry<String, String>> hdrs;

    /**
     * Body to use.
     */
    @Immutable.Array
    private final transient byte[] content;

    /**
     * Public ctor.
     * @param wre Wire
     * @param uri The resource to work with
     */
    BaseRequest(final Wire wre, final String uri) {
        this(
                wre, uri,
                new Array<Map.Entry<String, String>>(),
                Request.GET, BaseRequest.EMPTY_BYTE_ARRAY
        );
    }

    /**
     * Public ctor.
     * @param wre Wire
     * @param uri The resource to work with
     * @param headers Headers
     * @param method HTTP method
     * @param body HTTP request body
     * @checkstyle ParameterNumber (5 lines)
     */
    BaseRequest(final Wire wre, final String uri,
                final Iterable<Map.Entry<String, String>> headers,
                final String method, final byte[] body) {
        this(wre, uri, headers, method, body, 0, 0);
    }

    /**
     * Public ctor.
     * @param wre Wire
     * @param uri The resource to work with
     * @param headers Headers
     * @param method HTTP method
     * @param body HTTP request body
     * @param cnct Connect timeout for http connection
     * @param rdd Read timeout for http connection
     * @checkstyle ParameterNumber (5 lines)
     */
    BaseRequest(final Wire wre, final String uri,
                final Iterable<Map.Entry<String, String>> headers,
                final String method, final byte[] body,
                final int cnct, final int rdd) {
        this.wire = wre;
        URI addr = URI.create(uri);
        if (addr.getPath() != null && addr.getPath().isEmpty()) {
            addr = UriBuilder.fromUri(addr).path("/").build();
        }
        this.home = addr.toString();
        this.hdrs = new Array<Map.Entry<String, String>>(headers);
        this.mtd = method;
        this.content = body.clone();
        this.connect = cnct;
        this.read = rdd;
    }

    @Override
    public RequestURI uri() {
        return new BaseRequest.BaseURI(this, this.home);
    }

    @Override
    public Request header(final String name, final Object value) {
        return new BaseRequest(
                this.wire,
                this.home,
                this.hdrs.with(new ImmutableHeader(name, value.toString())),
                this.mtd,
                this.content,
                this.connect,
                this.read
        );
    }

    @Override
    public Request reset(final String name) {
        final Collection<Map.Entry<String, String>> headers =
                new LinkedList<>();
        final String key = ImmutableHeader.normalize(name);
        for (final Map.Entry<String, String> header : this.hdrs) {
            if (!header.getKey().equals(key)) {
                headers.add(header);
            }
        }
        return new BaseRequest(
                this.wire,
                this.home,
                headers,
                this.mtd,
                this.content,
                this.connect,
                this.read
        );
    }

    @Override
    public RequestBody body() {
        return new BaseRequest.FormEncodedBody(this, this.content);
    }

    @Override
    public RequestBody multipartBody() {
        return new BaseRequest.MultipartFormBody(this, this.content);
    }

    @Override
    public Request method(final String method) {
        return new BaseRequest(
                this.wire,
                this.home,
                this.hdrs,
                method,
                this.content,
                this.connect,
                this.read
        );
    }

    @Override
    public Request timeout(final int cnct, final int rdd) {
        return new BaseRequest(
                this.wire,
                this.home,
                this.hdrs,
                this.mtd,
                this.content,
                cnct,
                rdd
        );
    }

    @Override
    public Response fetch() throws IOException {
        return this.fetchResponse(new ByteArrayInputStream(this.content));
    }

    @Override
    public Response fetch(final InputStream stream) throws IOException {
        if (this.content.length > 0) {
            throw new IllegalStateException(
                    "Request Body is not empty, use fetch() instead"
            );
        }
        return this.fetchResponse(stream);
    }

    @Override
    public <T extends Wire> Request through(final Class<T> type,
                                            final Object... args) {
        Constructor<?> ctor = null;
        for (final Constructor<?> opt : type.getDeclaredConstructors()) {
            if (opt.getParameterTypes().length == args.length + 1) {
                ctor = opt;
                break;
            }
        }
        if (ctor == null) {
            throw new IllegalArgumentException(
                    String.format(
                            "class %s doesn't have a ctor with %d argument(s)",
                            type.getName(), args.length
                    )
            );
        }
        final Object[] params = new Object[args.length + 1];
        params[0] = this.wire;
        System.arraycopy(args, 0, params, 1, args.length);
        final Wire decorated;
        try {
            decorated = Wire.class.cast(ctor.newInstance(params));
        } catch (final InstantiationException
                | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
        return new BaseRequest(
                decorated,
                this.home,
                this.hdrs,
                this.mtd,
                this.content,
                this.connect,
                this.read
        );
    }

    @Override
    @SuppressWarnings("PMD.ConsecutiveLiteralAppends")
    public String toString() {
        final URI uri = URI.create(this.home);
        final StringBuilder text = new StringBuilder("HTTP/1.1 ")
                .append(this.mtd).append(' ')
                .append(uri.getPath())
                .append(" (")
                .append(uri.getHost())
                .append(")\n");
        for (final Map.Entry<String, String> header : this.hdrs) {
            text.append(
                    Logger.format(
                            "%s: %s\n",
                            header.getKey(),
                            header.getValue()
                    )
            );
        }
        return text.append('\n')
                .append(new RequestBody.Printable(this.content).toString())
                .toString();
    }

    /**
     * Fetch response from server.
     * @param stream The content to send.
     * @return The obtained response
     * @throws IOException If an IO exception occurs.
     */
    private Response fetchResponse(final InputStream stream)
            throws IOException {
        final long start = System.currentTimeMillis();
        final Response response = this.wire.send(
                this, this.home, this.mtd,
                this.hdrs, stream, this.connect,
                this.read
        );
        final URI uri = URI.create(this.home);
        Logger.info(
            this,
            "#fetch(%s %s%s %s): [%d %s] in %[ms]s",
            this.mtd,
            uri.getHost(),
            // @checkstyle AvoidInlineConditionalsCheck (1 line)
            uri.getPort() > 0 ? String.format(":%d", uri.getPort()) : "",
            uri.getPath(),
            response.status(),
            response.reason(),
            System.currentTimeMillis() - start
        );
        return response;
    }

    /**
     * Base URI.
     */
    @Immutable
    @Loggable(Loggable.DEBUG)
    private static final class BaseURI implements RequestURI {
        /**
         * URI encapsulated.
         */
        private final transient String address;
        /**
         * Base request encapsulated.
         */
        private final transient BaseRequest owner;
        /**
         * Public ctor.
         * @param req Request
         * @param uri The URI to start with
         */
        BaseURI(final BaseRequest req, final String uri) {
            this.owner = req;
            this.address = uri;
        }
        @Override
        public String toString() {
            return this.address;
        }
        @Override
        public Request back() {
            return new BaseRequest(
                    this.owner.wire,
                    this.address,
                    this.owner.hdrs,
                    this.owner.mtd,
                    this.owner.content
            );
        }
        @Override
        public URI get() {
            return URI.create(this.owner.home);
        }
        @Override
        public RequestURI set(final URI uri) {
            return new BaseRequest.BaseURI(this.owner, uri.toString());
        }
        @Override
        public RequestURI queryParam(final String name, final Object value) {
            return new BaseRequest.BaseURI(
                    this.owner,
                    UriBuilder.fromUri(this.address)
                            .queryParam(name, "{value}")
                            .build(value).toString()
            );
        }
        @Override
        public RequestURI queryParams(final Map<String, String> map) {
            final UriBuilder uri = UriBuilder.fromUri(this.address);
            final Object[] values = new Object[map.size()];
            int idx = 0;
            for (final Map.Entry<String, String> pair : map.entrySet()) {
                uri.queryParam(pair.getKey(), String.format("{x%d}", idx));
                values[idx] = pair.getValue();
                ++idx;
            }
            return new BaseRequest.BaseURI(
                    this.owner,
                    uri.build(values).toString()
            );
        }
        @Override
        public RequestURI path(final String segment) {
            return new BaseRequest.BaseURI(
                    this.owner,
                    UriBuilder.fromUri(this.address)
                            .path(segment)
                            .build().toString()
            );
        }
        @Override
        public RequestURI userInfo(final String info) {
            return new BaseRequest.BaseURI(
                    this.owner,
                    UriBuilder.fromUri(this.address)
                            .userInfo(info)
                            .build().toString()
            );
        }
        @Override
        public RequestURI port(final int num) {
            return new BaseRequest.BaseURI(
                    this.owner,
                    UriBuilder.fromUri(this.address)
                            .port(num).build().toString()
            );
        }
    }

    /**
     * Body of a request with a form that has attachments.
     * @todo #87:1h Implement and unit test method formParam(String, Object)
     *  Details <a href="http://stackoverflow.com/
     *  questions/8659808/how-does-http-file-upload-work">here</a>
     *  (second answer). <br> e.g. While FormEncodedBody.formParam adds
     *  the param to a body with enctype application/x-www-form-urlencoded,
     *  method MultipartFormBody.formParam should add it to a body with
     *  enctype multipart/form-data.
     */
    private static final class MultipartFormBody implements RequestBody {

        /**
         * Content encapsulated.
         */
        @Immutable.Array
        private final transient byte[] text;

        /**
         * Base request encapsulated.
         */
        private final transient BaseRequest owner;

        /**
         * Public ctor.
         * @param req Request
         * @param body Text to encapsulate
         */
        MultipartFormBody(final BaseRequest req, final byte[] body) {
            this.owner = req;
            this.text = body.clone();
        }

        @Override
        public String toString() {
            return new RequestBody.Printable(this.text).toString();
        }

        @Override
        public Request back() {
            return new BaseRequest(
                    this.owner.wire,
                    this.owner.home,
                    this.owner.hdrs,
                    this.owner.mtd,
                    this.text,
                    this.owner.connect,
                    this.owner.read
            );
        }

        @Override
        public String get() {
            return new String(this.text, BaseRequest.CHARSET);
        }

        @Override
        public RequestBody set(final String txt) {
            return this.set(txt.getBytes(BaseRequest.CHARSET));
        }

        @Override
        public RequestBody set(final JsonStructure json) {
            final StringWriter writer = new StringWriter();
            Json.createWriter(writer).write(json);
            return this.set(writer.toString());
        }

        @Override
        public RequestBody set(final byte[] txt) {
            return new BaseRequest.FormEncodedBody(this.owner, txt);
        }

        @Override
        public RequestBody formParam(final String name, final Object value) {
            throw new UnsupportedOperationException("Method not available");
        }

        @Override
        public RequestBody formParams(final Map<String, String> params) {
            RequestBody body = this;
            for (final Map.Entry<String, String> param : params.entrySet()) {
                body = body.formParam(param.getKey(), param.getValue());
            }
            return body;
        }
    }

    /**
     * Body of a request with a simple form.
     * (enctype application/x-www-form-urlencoded)
     */
    @Immutable
    @Loggable(Loggable.DEBUG)
    private static final class FormEncodedBody implements RequestBody {

        /**
         * Content encapsulated.
         */
        @Immutable.Array
        private final transient byte[] text;

        /**
         * Base request encapsulated.
         */
        private final transient BaseRequest owner;

        /**
         * URL form character to prepend.
         */
        private final transient String prepend;

        /**
         * Public ctor.
         * @param req Request
         * @param body Text to encapsulate
         */
        FormEncodedBody(final BaseRequest req, final byte[] body) {
            this(req, body, "");
        }

        /**
         * Public ctor.
         * @param req Request
         * @param body Text to encapsulate
         * @param pre Character to prepend
         */
        FormEncodedBody(
                final BaseRequest req, final byte[] body, final String pre
        ) {
            this.owner = req;
            this.text = body.clone();
            this.prepend = pre;
        }

        @Override
        public String toString() {
            return new RequestBody.Printable(this.text).toString();
        }

        @Override
        public Request back() {
            return new BaseRequest(
                    this.owner.wire,
                    this.owner.home,
                    this.owner.hdrs,
                    this.owner.mtd,
                    this.text,
                    this.owner.connect,
                    this.owner.read
            );
        }

        @Override
        public String get() {
            return new String(this.text, BaseRequest.CHARSET);
        }

        @Override
        public RequestBody set(final String txt) {
            return this.set(txt.getBytes(BaseRequest.CHARSET));
        }

        @Override
        public RequestBody set(final JsonStructure json) {
            final StringWriter writer = new StringWriter();
            Json.createWriter(writer).write(json);
            return this.set(writer.toString());
        }

        @Override
        public RequestBody set(final byte[] txt) {
            return new BaseRequest.FormEncodedBody(this.owner, txt);
        }

        @Override
        public RequestBody formParam(final String name, final Object value) {
            try {
                return new BaseRequest.FormEncodedBody(
                        this.owner,
                        new StringBuilder(this.get())
                                .append(this.prepend)
                                .append(name)
                                .append('=')
                                .append(
                                        URLEncoder.encode(
                                                value.toString(),
                                                BaseRequest.ENCODING
                                        )
                                )
                                .toString()
                                .getBytes(BaseRequest.CHARSET),
                        "&"
                );
            } catch (final UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        public RequestBody formParams(final Map<String, String> params) {
            RequestBody body = this;
            for (final Map.Entry<String, String> param : params.entrySet()) {
                body = body.formParam(param.getKey(), param.getValue());
            }
            return body;
        }

    }

}

