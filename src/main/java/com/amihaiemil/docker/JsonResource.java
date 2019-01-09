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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * A JsonObject resource returned by the API (could be a Container,
 * an Image etc).
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #114:30min Continue making other Json resources from
 *  the API (e.g. Image), extend this class, so we can avoid the N+1
 *  query problem. Through this mechanism, each resource holds the JsonObject
 *  that was returned by the API at the moment of its creation. If the client
 *  would like to make sure that the resource's JsonObject is updated, they
 *  should call the inspect() method.
 */
abstract class JsonResource implements JsonObject {
    
    /**
     * The JsonObject resource in question.
     */
    private final JsonObject resource;

    /**
     * Ctor.
     * @param resource Supply the JsonObject.
     */
    JsonResource(final Supplier<JsonObject> resource) {
        this(resource.get());
    }
    
    /**
     * Ctor.
     * @param resource The JsonObject resource in question.
     */
    JsonResource(final JsonObject resource) {
        this.resource = resource;
    }
    
    @Override
    public final JsonArray getJsonArray(final String name) {
        return this.resource.getJsonArray(name);
    }

    @Override
    public final JsonObject getJsonObject(final String name) {
        return this.resource.getJsonObject(name);
    }

    @Override
    public final JsonNumber getJsonNumber(final String name) {
        return this.resource.getJsonNumber(name);
    }

    @Override
    public final JsonString getJsonString(final String name) {
        return this.resource.getJsonString(name);
    }

    @Override
    public final String getString(final String name) {
        return this.resource.getString(name);
    }

    @Override
    public final String getString(final String name, 
        final String defaultValue
    ) {
        return this.resource.getString(name, defaultValue);
    }

    @Override
    public final int getInt(final String name) {
        return this.resource.getInt(name);
    }

    @Override
    public final int getInt(final String name, final int defaultValue) {
        return this.resource.getInt(name, defaultValue);
    }

    @Override
    public final boolean getBoolean(final String name) {
        return this.resource.getBoolean(name);
    }

    @Override
    public final boolean getBoolean(final String name, 
        final boolean defaultValue
    ) {
        return this.resource.getBoolean(name, defaultValue);
    }

    @Override
    public final boolean isNull(final String name) {
        return this.resource.isNull(name);
    }

    @Override
    public final ValueType getValueType() {
        return this.resource.getValueType();
    }

    @Override
    public final int size() {
        return this.resource.size();
    }

    @Override
    public final boolean isEmpty() {
        return this.resource.isEmpty();
    }

    @Override
    public final boolean containsKey(final Object key) {
        return this.resource.containsKey(key);
    }

    @Override
    public final boolean containsValue(final Object value) {
        return this.resource.containsValue(value);
    }

    @Override
    public final JsonValue get(final Object key) {
        return this.resource.get(key);
    }

    @Override
    public final JsonValue put(final String key, final JsonValue value) {
        return this.resource.put(key, value);
    }

    @Override
    public final JsonValue remove(final Object key) {
        return this.resource.remove(key);
    }

    @Override
    public final void putAll(
        final Map<? extends String, ? extends JsonValue> map
    ) {
        this.resource.putAll(map);
    }

    @Override
    public final void clear() {
        this.resource.clear();
    }

    @Override
    public final Set<String> keySet() {
        return this.resource.keySet();
    }

    @Override
    public final Collection<JsonValue> values() {
        return this.resource.values();
    }

    @Override
    public final Set<Entry<String, JsonValue>> entrySet() {
        return this.resource.entrySet();
    }

    @Override
    public final String toString() {
        return this.resource.toString();
    }
    
    @Override
    public final boolean equals(final Object other) {
        return this.resource.equals(other);
    }

    @Override
    public final int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.resource);
        return hash;
    }
}
