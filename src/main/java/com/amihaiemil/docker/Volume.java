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

/**
 * A volume manager.
 * @author Marco Teixeira (marcoo.teixeira@gmail.com)
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @version $Id$
 * @see <a href="https://docs.docker.com/engine/api/v1.35/#tag/Volume">Docker Volume API</a>
 * @since 0.0.6
 * @todo #179:30min Continue implementing create and remove volume operations.
 *
 */
public interface Volume extends JsonObject {

    /**
     * Return low-level information about this volume.
     * @return JsonObject information.
     * @see <a href="https://docs.docker.com/engine/api/v1.35/#operation/VolumeInspect">Inspect Volume</a>
     * @throws IOException If something goes wrong.
     * @throws UnexpectedResponseException If the status response is not
     *  the expected one (200 OK).
     */
    JsonObject inspect() throws IOException, UnexpectedResponseException;

}
