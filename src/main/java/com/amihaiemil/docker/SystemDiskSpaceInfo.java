package com.amihaiemil.docker;

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * Docker disk space usage information.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.6
 */
public final class SystemDiskSpaceInfo implements DiskSpaceInfo {

    /**
     * Response Json object from system/df call.
     */
    private final JsonObject json;

    /**
     * Ctor.
     * @param jsonObject Response Json from system/df call
     */
    SystemDiskSpaceInfo(final JsonObject jsonObject) {
        this.json = jsonObject;
    }

    @Override
    public Long images() {
        return this.json.getJsonNumber("LayersSize").longValue();
    }

    @Override
    public Long containers() {
        Long totalContainersSpace = 0L;
        JsonArray containers = this.json.getJsonArray("Containers");
        for (int i=0; i<containers.size(); i++) {
            JsonObject container = containers.getJsonObject(i);
            totalContainersSpace +=
                    container.getJsonNumber("SizeRootFs").longValue();
        }
        return totalContainersSpace;
    }

    @Override
    public Long volumes() {
        Long totalVolumesSpace = 0L;
        JsonArray volumes = this.json.getJsonArray("Volumes");
        for (int i=0; i<volumes.size(); i++) {
            JsonObject volume = volumes.getJsonObject(i);
            totalVolumesSpace +=
                    volume.getJsonObject("UsageData")
                            .getJsonNumber("Size").longValue();
        }
        return totalVolumesSpace;
    }

    @Override
    public Long totalSpace() {
        return this.images() + this.containers() + this.volumes();
    }
}
