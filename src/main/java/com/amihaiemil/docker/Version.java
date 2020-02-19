package com.amihaiemil.docker;

import javax.json.JsonObject;

/**
 * Version API.
 * @author Michael Lux (michi.lux@gmail.com)
 * @since 0.0.11
 */
public interface Version extends JsonObject {
    /**
     * Returns the version of the connected docker engine.
     * @return Version of connected docker engine
     */
    String version();

    /**
     * Returns the name of the connected docker platform.
     * @return Name of the docker platform
     */
    String platformName();

    /**
     * Returns the API version of the docker engine.
     * @return API version
     */
    String apiVersion();

    /**
     * Returns the minimum API version of the docker engine.
     * @return Minimum API version
     */
    String minApiVersion();

    /**
     * Returns the OS docker is running on.
     * @return Name of the OS docker is running on
     */
    String os();

    /**
     * Returns the (CPU) architecture docker is running on.
     * @return The (CPU) architecture docker is running on
     */
    String arch();

    /**
     * Reports whether experimental docker features are enabled.
     * @return Whether experimental docker features are enabled
     */
    boolean experimental();
}
