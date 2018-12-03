package com.amihaiemil.docker;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;

/**
 * Integration tests for RtDockerSystem.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.6
 */
public final class RtDockerSystemITCase {

    /**
     * Show Docker disk space info.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void showDiskSpaceInfo() throws Exception {
        final Docker docker = new LocalDocker(
                new File("/var/run/docker.sock")
        );

        DiskSpaceInfo info = docker.system().diskUsage();

        MatcherAssert.assertThat(info.totalSpace(),
                Matchers.greaterThanOrEqualTo(0L));
    }

}
