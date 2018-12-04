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
