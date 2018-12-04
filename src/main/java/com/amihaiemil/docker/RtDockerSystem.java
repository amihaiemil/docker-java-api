package com.amihaiemil.docker;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.net.URI;

/**
 * System API.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.6
 */
final class RtDockerSystem implements DockerSystem {

    /**
     * Apache HttpClient which sends the requests.
     */
    private final HttpClient client;

    /**
     * Base URI.
     */
    private final URI baseUri;

    /**
     * Docker engine.
     */
    private Docker docker;

    /**
     * Ctor.
     *
     * @param client Given HTTP Client.
     * @param baseUri Base URI, ending with /system.
     * @param dkr The Docker engine.
     */
    RtDockerSystem(
        final HttpClient client, final URI baseUri, final Docker dkr
    ) {
        this.client = client;
        this.baseUri = baseUri;
        this.docker = dkr;
    }

    @Override
    public DiskSpaceInfo diskUsage()
        throws IOException, UnexpectedResponseException {
        final HttpGet init = new HttpGet(this.baseUri.toString() + "/df");
        try {
            return new SystemDiskSpaceInfo(
                this.client.execute(
                    init,
                    new ReadJsonObject(
                        new MatchStatus(
                            init.getURI(),
                            HttpStatus.SC_OK
                        )
                    )
                )
            );
        } finally {
            init.releaseConnection();
        }
    }

}
