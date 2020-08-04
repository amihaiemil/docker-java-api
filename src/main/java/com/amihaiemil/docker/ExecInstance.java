package com.amihaiemil.docker;

import java.io.IOException;
import javax.json.JsonObject;

/**
 * Exec Api
 * @version 1.00 2019-12-24
 * @author Jack Pan
 * */
public interface ExecInstance {

    /**
     * Starts a previously set up exec instance. If detach is true,
     * this endpoint returns immediately after starting the command.
     * Otherwise, it sets up an interactive session with the command.
     * @param exec
     * */
    String start(final JsonObject exec) throws IOException, UnexpectedResponseException;

    void resize() throws IOException, UnexpectedResponseException;

    JsonObject inspect() throws IOException, UnexpectedResponseException;

}
