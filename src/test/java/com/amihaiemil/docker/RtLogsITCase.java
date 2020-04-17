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

import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;
import org.junit.Ignore;
import java.io.File;

/**
 * Integration tests for {@link RtLogs}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 * @todo #256:30min Fix the IT case for follow(), it is currently failing.
 *  We might have to implement the stream decoding (in case TTY is disabled
 *  when the Container is created), as explained here, in "Stream format"
 *  paragraph:
 *  https://docs.docker.com/engine/api/v1.37/#operation/ContainerAttach
 */
public final class RtLogsITCase {

    /**
     * RtLogs can fetch the Container's logs (return them as a String).
     * @throws Exception If something goes wrong.
     */
    @Test
    public void fetchesLogs() throws Exception {
        final Container container =  new UnixDocker(
            new File("/var/run/docker.sock")
        ).images().pull("hello-world", "latest").run();
        final String logs = container.logs().fetch();
        MatcherAssert.assertThat(
            logs,
            new StringStartsWith("\nHello from Docker!")
        );
    }

    /**
     * RtLogs can follow the Container's logs (return them as a Reader).
     * @throws Exception If something goes wrong.
     */
    @Test
    @Ignore
    public void followsLogs() throws Exception {
        final Container container =  new UnixDocker(
            new File("/var/run/docker.sock")
        ).images().pull("ubuntu", "latest").run();
        final String logs = IOUtils.toString(container.logs().follow());
        MatcherAssert.assertThat(
            logs.trim(),
            new StringStartsWith("Hello from Docker!")
        );
    }
}
