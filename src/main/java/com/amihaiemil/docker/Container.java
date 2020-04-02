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

import javax.json.JsonObject;
import java.io.IOException;

/**
 * A Docker container.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Container extends JsonObject {

    /**
     * Inspect this container, return low-level information
     * about it in Json format. It is recommended to wrap this
     * Json into a live object, with an interface, which would animate
     * it.
     * @return Container info in Json format.
     * @throws IOException If something goes wrong.
     */
    JsonObject inspect() throws IOException;

    /**
     * Start this container.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerStart">Start Container</a>
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (204 NO CONTENT).
     */
    void start() throws IOException, UnexpectedResponseException;

    /**
     * This Container's id.
     * @return String id.
     */
    String containerId();

    /**
     * Stop this container.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerStop">Stop Container</a>
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (204 NO CONTENT).
     */
    void stop() throws IOException, UnexpectedResponseException;

    /**
     * Kill this container. SIGKILL is sent to this container.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerKill">Kill Container</a>
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (204 NO CONTENT).
     */
    void kill() throws IOException, UnexpectedResponseException;

    /**
     * Restarts this container.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerRestart">Restart Container</a>
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (204 NO CONTENT).
     */
    void restart() throws IOException, UnexpectedResponseException;
    
    /**
     * Rename this container.
     * @param name New name for the container.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerRename">Rename Container</a>
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (204 NO CONTENT).
     */
    void rename(final String name)
        throws IOException, UnexpectedResponseException;
    
    /**
     * Remove this container.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerDelete">Delete Container</a>
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (204 NO CONTENT).
     */
    void remove() throws IOException, UnexpectedResponseException;
    
    
    /**
     * Remove this container.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerDelete">Delete Container</a>
     * @param volumes Remove the volumes associated with the container.
     * @param force If the container is running, kill it before removing it.
     * @param link Remove the specified link associated with the container.
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (204 NO CONTENT).
     */
    void remove(final boolean volumes, final boolean force, final boolean link)
        throws IOException, UnexpectedResponseException;
    
    /**
     * The Logs of this container.<br><br>
     * Note that this operation works only for containers with the json-file
     * or journald logging driver.
     * @return Logs of this container.
     */
    Logs logs();
    
    /**
     * Return the Docker engine where this Container came from.
     * @return Docker.
     */
    Docker docker();

    /**
     * Pause this container.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerPause">Pause Container</a>
     * @throws IOException If something goes wrong.
     *  the expected one (204 NO CONTENT).
     */
    void pause() throws IOException;

    /**
     * Unpause this container.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerUnpause">Unpause Container</a>
     * @throws IOException If something goes wrong.
     *  the expected one (204 NO CONTENT).
     */
    void unpause() throws IOException;


    /**
     * Waits on this container.
     * @param state The state to wait for. One of "not-running"
     * (the default if null), "next-exit", or "removed"
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ContainerWait">
     * Wait Container</a>.
     * @throws IOException If something goes wrong.
     *  the expected one (200).
     * @return the exit code of the container
     */
    int waitOn(String state) throws IOException;

}
