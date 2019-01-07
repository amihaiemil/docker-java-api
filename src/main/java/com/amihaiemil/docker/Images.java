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
import java.io.Reader;
import java.util.Map;

/**
 * Images API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @see <a href="https://docs.docker.com/engine/api/v1.35/#tag/Image">Docker Images API</a>
 * @since 0.0.1
 * @todo #152:30min Add Fake implementations of Images and Image, in order to
 *  unit test method save() and other future methods which may require more
 *  than 1 HTTP request. Currently, the unit testing infrastructure does
 *  not support more than 1 HTTP request..
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
     * Import images from tar file.
     *
     * @param file Path to Tar file containing Images.
     * @throws IOException If an I/O error occurs.
     * @throws UnexpectedResponseException If the API responds with an
     *  unexpected status.
     */
    void importFromTar(
        String file) throws IOException, UnexpectedResponseException;

    /**
     * Deletes unused images.
     * @throws IOException If an I/O error occurs.
     * @throws UnexpectedResponseException If the API responds with an
     *  unexpected status.
     */
    void prune() throws IOException, UnexpectedResponseException;

    /**
     * Save these images in a tarball, by their ID.
     * @return Reader representing the tarball.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ImageGetAll">Export Images</a>
     * @throws IOException If an I/P error occurs.
     * @throws UnexpectedResponseException If the API responds with an
     *  unexpected status.
     */
    Reader save() throws IOException, UnexpectedResponseException;

    /**
     * Filter these images.
     * @param filters Filters to apply.
     * @return Filtered images.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/ImageList">Docker API Docs</a>
     */
    Images filter(Map<String, Iterable<String>> filters);

    /**
     * Return the Docker engine where these Images came from.
     * @return Docker.
     */
    Docker docker();
}
