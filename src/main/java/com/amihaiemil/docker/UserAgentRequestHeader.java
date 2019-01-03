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

import java.util.Collections;
import org.apache.http.HttpHeaders;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.message.BasicHeader;

/**
 * User Agent Request Header Interceptor.
 * @author Boris Kuzmic (boris.kuzmic@gmail.com)
 * @since 0.0.7
 * @todo #244:30min Replace hardcoded version with the one loaded from
 *  properties file. For example, create new version.properties file
 *  with property: build.version=${project.version}, add to src/main/resources
 *  and Maven filtering will do the rest.
 */
final class UserAgentRequestHeader extends RequestDefaultHeaders {

    /**
     * Version constant.
     */
    private static final String VERSION = "0.0.8-SNAPSHOT";

    /**
     * Ctor.
     */
    UserAgentRequestHeader() {
        super(Collections.singletonList(
            new BasicHeader(
                HttpHeaders.USER_AGENT,
                String.join(
                    " ",
                    "docker-java-api /",
                    VERSION,
                    "See https://github.com/amihaiemil/docker-java-api"
                )
            )
        ));
    }

}
