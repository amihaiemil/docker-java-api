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

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import java.net.URI;

/**
 * Unit tests for RtDockerSystem.
 *
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.6
 * @checkstyle MethodName (500 lines)
 */
public final class RtDockerSystemTestCase {

    /**
     * Must return the same disk space usage for images, containers and
     * volumes as in json array returned by the service.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void returnsDiskSpaceUsage() throws Exception {
        long totalSpace = new RtDockerSystem(
            new AssertRequest(
                new Response(
                    HttpStatus.SC_OK,
                    Json.createObjectBuilder()
                        .add("LayersSize", 250)
                        .add(
                            "Containers",
                            Json.createArrayBuilder()
                                .add(
                                    Json.createObjectBuilder()
                                    .add("SizeRootFs", 50)
                                ).add(
                                    Json.createObjectBuilder()
                                    .add("SizeRootFs", 60)
                                )
                            )
                        .add(
                            "Volumes",
                            Json.createArrayBuilder()
                                .add(
                                    Json.createObjectBuilder()
                                        .add(
                                            "UsageData",
                                            Json.createObjectBuilder()
                                                .add("Size", 200)
                                        )
                                ).add(
                                    Json.createObjectBuilder()
                                        .add(
                                            "UsageData",
                                            Json.createObjectBuilder()
                                                .add("Size", 100)
                                        )
                            )
                        ).build().toString()
                )
            ),
            URI.create("http://localhost/system"),
            Mockito.mock(Docker.class)
        ).diskUsage().totalSpace();
        MatcherAssert.assertThat(
            totalSpace,
            Matchers.is(660L)
        );
    }


}
