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
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * A docker plugin.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @see <a href="https://docs.docker.com/engine/api/v1.35/#tag/Plugin">Docker Plugin API</a>
 * @todo #251:30min Continue implementing rest of the Plugin methods.
 *  More information about API methods can be found at:
 *  https://docs.docker.com/engine/api/v1.35/#tag/Plugin
 * @since 0.0.7
 */
public interface Plugin extends JsonObject {

    /**
     * Return low-level information about this plugin.
     * @return JsonObject information.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/PluginInspect">Inspect a Plugin</a>
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (200 OK).
     */
    JsonObject inspect() throws IOException, UnexpectedResponseException;

    /**
     * Enable plugin.
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (200 OK).
     */
    void enable() throws IOException, UnexpectedResponseException;

    /**
     * Disable plugin.
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (200 OK).
     */
    void disable() throws IOException, UnexpectedResponseException;

    /**
     * Upgrade plugin with remote reference.
     * @param remote Reference to remote plugin.
     * @param properties Optional properties of plugin.
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (200 OK).
     */
    void upgrade(String remote, JsonArray properties)
        throws IOException, UnexpectedResponseException;

    /**
     * Push the plugin to registry.
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (200 OK).
     */
    void push() throws IOException, UnexpectedResponseException;

    /**
     * Configure plugin with options.
     * @param options Map of key-value pairs
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (200 OK).
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/PluginSet">Configure a plugin</a>
     */
    void configure(Map<String, String> options)
        throws IOException, UnexpectedResponseException;

}
