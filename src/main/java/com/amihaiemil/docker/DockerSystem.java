package com.amihaiemil.docker;

import java.io.IOException;

/**
 * Docker System API entry point.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @see <a href="https://docs.docker.com/engine/reference/commandline/system/">Docker DockerSystem API</a>
 * @since 0.0.6
 */
public interface DockerSystem {

    /**
     * Show docker disk usage.
     * @return The created {@link DiskSpaceInfo}.
     * @throws IOException If an I/O error occurs.
     * @throws UnexpectedResponseException If the API responds with an
     *  unexpected status.
     */
    DiskSpaceInfo diskUsage()
            throws IOException, UnexpectedResponseException;

}
