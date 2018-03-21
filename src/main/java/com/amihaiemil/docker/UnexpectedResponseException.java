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

/**
 * Runtime exception thrown in case the response is not part of the happy flow
 * For instance, when Container#inspect() gets a different response status than
 * 200 OK.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class UnexpectedResponseException extends RuntimeException {

    /**
     * Called endpoint.
     */
    private final String endpoint;

    /**
     * Actual response status.
     */
    private final int actualStatus;

    /**
     * Expected response status.
     */
    private final int expectedStatus;

    /**
     * Ctor.
     * @param endpoint Endpoint that was called.
     * @param actualStatus Received status.
     * @param expectedStatus Expected status.
     */
    public UnexpectedResponseException(
        final String endpoint, final int actualStatus, final int expectedStatus
    ) {
        // @checkstyle LineLength (1 line)
        super("Expected status " + expectedStatus + " but got " + actualStatus + " when calling " + endpoint);
        this.endpoint = endpoint;
        this.actualStatus = actualStatus;
        this.expectedStatus = expectedStatus;
    }

    /**
     * Called endpoint.
     * @return String.
     */
    public String endpoint() {
        return this.endpoint;
    }

    /**
     * Actual status.
     * @return Integer HTTP status.
     */
    public int actualStatus() {
        return this.actualStatus;
    }

    /**
     * Expected status.
     * @return Integer HTTP status.
     */
    public int expectedStatus() {
        return this.expectedStatus;
    }

}
