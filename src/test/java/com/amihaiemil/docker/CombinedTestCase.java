/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amihaiemil.docker;

import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for {@link Combined}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.2
 */
public final class CombinedTestCase {
    
    /**
     * {@link Combined} can combine some JsonObjects.
     */
    @Test
    public void combinesObjects() {
        final JsonObject first = Json.createObjectBuilder()
            .add("firstName", "John")
            .add("age", 23)
            .build();
        final JsonObject second = Json.createObjectBuilder()
            .add("lastName", "George")
            .add("job", "developer")
            .build();

        final JsonObject expected = Json.createObjectBuilder()
            .add("firstName", "John")
            .add("lastName", "George")
            .add("age", 23)
            .add("job", "developer")
            .build();
        MatcherAssert.assertThat(
            new Combined(first, second),
            Matchers.equalTo(expected)
        );
    }
    
    /**
     * {@link Combined} can combine one single JsonObject.
     */
    @Test
    public void combinesSingleObject() {
        final JsonObject single = Json.createObjectBuilder()
            .add("firstName", "John")
            .add("age", 23)
            .build();
        MatcherAssert.assertThat(
            new Combined(single),
            Matchers.equalTo(single)
        );
    }
    
    /**
     * {@link Combined} can combine one JsonObject and another empty one.
     */
    @Test
    public void combinesWithEmptyObject() {
        final JsonObject single = Json.createObjectBuilder()
            .add("firstName", "John")
            .add("age", 23)
            .build();
        MatcherAssert.assertThat(
            new Combined(single, Json.createObjectBuilder().build()),
            Matchers.equalTo(single)
        );
    }
}
