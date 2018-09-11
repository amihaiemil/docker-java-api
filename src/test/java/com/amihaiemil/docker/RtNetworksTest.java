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

import com.amihaiemil.docker.mock.AssertRequest;
import com.amihaiemil.docker.mock.Response;
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

/**
 * Tests for {@link RtNetworks}.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
public final class RtNetworksTest {
    /**
     * RtNetworks must iterate all networks returned by docker's API.
     */
    @Test
    public void iterateNetworks() {
        MatcherAssert.assertThat(
            "Cannot iterate networks in JSON",
            new RtNetworks(
                new AssertRequest(
                    new Response(
                        200,
                        // @checkstyle LineLength (20 lines)
                        "[\n"
                        + "  {\n"
                        + "    \"Name\": \"bridge\",\n"
                        + "    \"Id\": \"f2de39df4171b0dc801e8002d1d999b77256983dfc63041c0f34030aa3977566\",\n"
                        + "    \"Created\": \"2016-10-19T06:21:00.416543526Z\"\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"Name\": \"none\",\n"
                        + "    \"Id\": \"e086a3893b05ab69242d3c44e49483a3bbbd3a26b46baa8f61ab797c1088d794\",\n"
                        + "    \"Created\": \"0001-01-01T00:00:00Z\"\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"Name\": \"host\",\n"
                        + "    \"Id\": \"13e871235c677f196c4e1ecebb9dc733b9b2d2ab589e30c539efeda84a24215e\",\n"
                        + "    \"Created\": \"0001-01-01T00:00:00Z\"\n"
                        + "  }\n"
                        + "]"
                    )
                ),
                URI.create("http://test.docker.com")
            ),
            new IsIterableWithSize<>(new IsEqual<>(3))
        );
    }
}
