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

import javax.json.JsonObject;
import java.io.IOException;
import java.util.Iterator;

/**
 * Containers API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Containers extends Iterable<Container> {

    /**
     * Create a container with a random name.
     * @param image The image to use.
     * @return Created Container.
     * @throws IOException If something goes wrong.
     */
    Container create(final String image) throws IOException;

    /**
     * Create a container.
     * @param name The container's name.
     * @param image The image to use.
     * @return Created Container.
     * @throws IOException If something goes wrong.
     */
    Container create(final String name, final String image) throws IOException;

    /**
     * Create a container.
     * @param name Container's name.
     * @param container Json config as specified in the API's docs.
     * @return Created Container.
     * @throws IOException If something goes wrong.
     */
    Container create(
        final String name, final JsonObject container
    ) throws IOException;

    /**
     * Create a container with a random name.
     * @param container Json config as specified in the API's docs.
     * @return Created Container.
     * @throws IOException If something goes wrong.
     */
    Container create(final JsonObject container) throws IOException;
    
    /**
     * Return all the Containers, not only the running ones (simply iterating
     * over this Containers instance will fetch only the running ones).
     * @return Iterator over all the containers.
     */
    Iterator<Container> all();
    
    /**
     * Return the Docker engine where these Containers came from.
     * @return Docker.
     */
    Docker docker();
}
