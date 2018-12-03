package com.amihaiemil.docker;

/**
 * Info about Docker disk space usage in bytes.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.6
 */
public interface DiskSpaceInfo {

    /**
     * Images disk usage.
     * @return Disk usage for images in bytes
     */
    Long images();

    /**
     * Containers disk usage.
     * @return Disk usage for containers in bytes
     */
    Long containers();

    /**
     * Volumes disk usage.
     * @return Disk usage for volumes in bytes
     */
    Long volumes();

    /**
     * Total disk usage.
     * @return Total disk usage in bytes
     */
    Long totalSpace();

}
