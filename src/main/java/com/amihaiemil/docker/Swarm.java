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
import javax.json.JsonObject;

/**
 * Docker engines can be clustered together in a swarm.
 * <p>
 * See the <a href="https://docs.docker.com/engine/swarm/">swarm mode</a> and
 * <a href="https://docs.docker.com/engine/api/v1.35/#tag/Swarm">swarm API</a>
 * documentation for more information.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #70:30min Continue implementing all of the Swarm operations. See
 *  https://docs.docker.com/engine/api/v1.35/#tag/Swarm for reference and also
 *  the roadmap laid out here:
 *  https://github.com/amihaiemil/docker-java-api/issues/3#issuecomment-375821822
 */
public interface Swarm {
    /**
     * Inspects this swarm, returning low-level information
     * about it in Json format. It is recommended to wrap this
     * Json into a live object, with an interface, which would animate
     * it.
     * @return Swarm info in Json format.
     * @throws IOException If something goes wrong.
     */
    JsonObject inspect() throws IOException;

    /**
     * Initialize a new swarm.
     * @param listenAddress Listen address used for inter-manager communication.
     *     This can either be in the form of '192.168.1.1:4567' or 'eth0:4567'
     *     where the port number is optional in both cases.
     * @return The swarm's token.
     * @throws IOException If something goes wrong.
     */
    String init(String listenAddress) throws IOException;

    /**
     * Initialize a new swarm by providing a full specification.
     * @param spec Full specification for the swarm.
     * @return The swarm's token.
     * @throws IOException If something goes wrong.
     */
    String init(JsonObject spec) throws IOException;
}
