package com.amihaiemil.docker.mock;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;

/**
 * JSON payload of an HttpRequest.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class PayloadOf implements JsonObject {
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
    public PayloadOf(final HttpRequest request) {
        try {
            if (request instanceof HttpEntityEnclosingRequest) {
                this.json = Json.createReader(
                    ((HttpEntityEnclosingRequest) request).getEntity()
                        .getContent()
                ).readObject();
            } else {
                this.json = Json.createObjectBuilder().build();
            }
        } catch (final IOException ex) {
            throw new IllegalStateException("Cannot read request payload", ex);
        }
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
