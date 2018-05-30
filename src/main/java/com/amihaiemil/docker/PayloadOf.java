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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * JSON payload of an HttpRequest.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #108:30min Add tests for PayloadOf.
 */
final class PayloadOf implements JsonObject {
    /**
     * The request's payload.
     */
    private final JsonObject json;

    /**
     * Ctor.
     * 
     * @param request The http request
     * @throws IllegalStateException if the request's payload cannot be read
     */
    PayloadOf(final HttpRequest request) {
        this(() -> {
            try {
                final JsonObject body;
                if (request instanceof HttpEntityEnclosingRequest) {
                    body = Json.createReader(
                        ((HttpEntityEnclosingRequest) request).getEntity()
                            .getContent()
                    ).readObject();
                } else {
                    body =  Json.createObjectBuilder().build();
                }
                return body;
            } catch (final IOException ex) {
                throw new IllegalStateException(
                    "Cannot read request payload", ex
                );
            }
        });
    }

    /**
     * Ctor.
     * @param response The http response.
     * @throws IllegalStateException if the response's payload cannot be read
     */
    PayloadOf(final HttpResponse response) {
        this(() -> {
            try {
                return Json.createReader(
                    response.getEntity().getContent()
                ).readObject();
            } catch (final IOException ex) {
                throw new IllegalStateException(
                    "Cannot read response payload", ex
                );
            }
        });
    }

    /**
     * Ctor.
     * @param json The json.
     * @throws IllegalStateException if the payload cannot be read
     */
    private PayloadOf(final Supplier<JsonObject> json) {
        this.json = json.get();
    }

    @Override
    public JsonArray getJsonArray(final String name) {
        return this.json.getJsonArray(name);
    }

    @Override
    public JsonObject getJsonObject(final String name) {
        return this.json.getJsonObject(name);
    }

    @Override
    public JsonNumber getJsonNumber(final String name) {
        return this.json.getJsonNumber(name);
    }

    @Override
    public JsonString getJsonString(final String name) {
        return this.json.getJsonString(name);
    }

    @Override
    public String getString(final String name) {
        return this.json.getString(name);
    }

    @Override
    public String getString(final String name, final String defaultValue) {
        return this.json.getString(name, defaultValue);
    }

    @Override
    public int getInt(final String name) {
        return this.json.getInt(name);
    }

    @Override
    public int getInt(final String name, final int defaultValue) {
        return this.json.getInt(name, defaultValue);
    }

    @Override
    public boolean getBoolean(final String name) {
        return this.json.getBoolean(name);
    }

    @Override
    public boolean getBoolean(final String name, final boolean defaultValue) {
        return this.json.getBoolean(name, defaultValue);
    }

    @Override
    public boolean isNull(final String name) {
        return this.json.isNull(name);
    }

    @Override
    public ValueType getValueType() {
        return this.json.getValueType();
    }

    @Override
    public int size() {
        return this.json.size();
    }

    @Override
    public boolean isEmpty() {
        return this.json.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.json.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.json.containsValue(value);
    }

    @Override
    public JsonValue get(final Object key) {
        return this.json.get(key);
    }

    @Override
    public JsonValue put(final String key, final JsonValue value) {
        return this.json.put(key, value);
    }

    @Override
    public JsonValue remove(final Object key) {
        return this.json.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends JsonValue> map) {
        this.json.putAll(map);
    }

    @Override
    public void clear() {
        this.json.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.json.keySet();
    }

    @Override
    public Collection<JsonValue> values() {
        return this.json.values();
    }

    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        return this.json.entrySet();
    }
}
