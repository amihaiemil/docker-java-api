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
import java.net.URL;

/**
 * Images API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @see <a href="https://docs.docker.com/engine/api/v1.35/#tag/Image">Docker Images API</a>
 * @since 0.0.1
 * @todo #98:30min Continue implementing the rest of the operations for the
 *  Images interface. See the docs referenced above for more details.
 */
public interface Images extends Iterable<Image> {

    /**
     * Pull an Image from the Docker registry.
     * @param name Name of the image to pull.
     * @param tag Tag or digest for the image.
     * @return The created {@link Image}.
     * @throws IOException If an I/O error occurs.
     * @throws UnexpectedResponseException If the API responds with an 
     *  unexpected status.
     * @checkstyle ParameterNumber (4 lines)
     */
    Image pull(
        final String name, final String tag
    ) throws IOException, UnexpectedResponseException;

    /**
     * Import an Image.
     * @param source The URL from which the image can be retrieved.
     * @param repo Repository name given to an image when it is imported.
     *   The repo may include a tag.
     * @return The imported Image.
     * @throws IOException If an I/O error occurs.
     * @throws UnexpectedResponseException If the API responds with an
     *  undexpected status.
     */
    Image importImage(
        final URL source, final String repo
    ) throws IOException, UnexpectedResponseException;

    /**
     * Deletes unused images.
     * @throws IOException If an I/O error occurs.
     * @throws UnexpectedResponseException If the API responds with an
     *  unexpected status.
     */
    void prune() throws IOException, UnexpectedResponseException;
    
    /**
     * Return the Docker engine where these Images came from.
     * @return Docker.
     */
    Docker docker();
}
