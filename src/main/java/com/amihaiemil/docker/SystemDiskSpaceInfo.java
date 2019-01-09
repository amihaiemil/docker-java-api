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

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * Docker disk space usage information.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.6
 */
final class SystemDiskSpaceInfo extends JsonResource
        implements DiskSpaceInfo {

    /**
     * Ctor.
     * @param jsonObject Response Json from system/df call
     */
    SystemDiskSpaceInfo(final JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public Long images() {
        return this.getJsonNumber("LayersSize").longValue();
    }

    @Override
    public Long containers() {
        JsonArray containers = this.getJsonArray("Containers");
        return containers.stream().map(JsonObject.class::cast)
                .mapToLong(c -> c.getJsonNumber("SizeRootFs").longValue())
                .sum();
    }

    @Override
    public Long volumes() {
        JsonArray volumes = this.getJsonArray("Volumes");
        return volumes.stream().map(JsonObject.class::cast)
                .mapToLong(v -> v.getJsonObject("UsageData")
                        .getJsonNumber("Size").longValue())
                .sum();
    }

    @Override
    public Long totalSpace() {
        return this.images() + this.containers() + this.volumes();
    }
}
