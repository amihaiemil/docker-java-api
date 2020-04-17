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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Handler that returns the response content as a String.
 * @author Morozov Evgeniy (lumimul@gmail.com)
 * @version $Id$
 * @since 0.0.2
 */
final class ReadLogString implements ResponseHandler<String> {

    /**
     * Handlers to be executed before actually reading the array.
     */
    private final ResponseHandler<HttpResponse> other;

    /**
     * Ctor.
     * @param other Handlers to be executed before actually reading the array.
     */
    ReadLogString(final ResponseHandler<HttpResponse> other) {
        this.other = other;
    }

    @Override
    public String handleResponse(final HttpResponse httpResponse)
            throws IOException {
        final HttpResponse resp = this.other.handleResponse(httpResponse);
        return this.toString(resp.getEntity());
    }

    /**
     * Docker logs contains header
     * [8]byte{STREAM_TYPE, 0, 0, 0, SIZE1, SIZE2, SIZE3, SIZE4}
     * STREAM_TYPE
     * 0: stdin (is written on stdout)
     * 1: stdout
     * 2: stderr
     *
     * SIZE1, SIZE2, SIZE3, SIZE4 are the four bytes of the uint32 size
     * encoded as big endian.
     *
     * This method do:
     *
     * 1) Read 8 bytes.
     * 2) Choose stdout or stderr depending on the first byte.
     * 3) Extract the frame size from the last four bytes.
     * 4) Read the extracted size and output it on the correct output.
     * 5) Goto 1.
     *
     * @param entity HttpEntity for read message.
     * @return Logs from container in String.
     * @throws IOException if the entity cannot be read
     */
    private String toString(final HttpEntity entity) throws IOException {
        final InputStream instream = entity.getContent();
        final CharArrayBuffer buffer = new CharArrayBuffer(
                this.getCapacity(entity)
        );
        if (instream != null) {
            try {
                final Reader reader = new InputStreamReader(
                        instream,
                        this.getCharset(ContentType.get(entity))
                );
                this.read(buffer, reader);
            } finally {
                instream.close();
            }
        }
        return buffer.toString();
    }

    /**
     * Docker logs contains header
     * [8]byte{STREAM_TYPE, 0, 0, 0, SIZE1, SIZE2, SIZE3, SIZE4}
     * STREAM_TYPE
     * 0: stdin (is written on stdout)
     * 1: stdout
     * 2: stderr
     *
     * SIZE1, SIZE2, SIZE3, SIZE4 are the four bytes of the uint32 size
     * encoded as big endian.
     *
     * 1) Read 8 bytes from reader.
     * 2) Choose not stdin(0) depending on the first byte.
     * 3) Extract the frame size from the last four bytes.
     * 4) Read the extracted size from reader and save it in buffer in circle.
     * 5) Goto 1.
     *
     * @param buffer Buffer for save message.
     * @param reader Reader for read message.
     * @throws IOException if the entity cannot be read
     */
    private void read(final CharArrayBuffer buffer,
                      final Reader reader) throws IOException {
        char[] controlChars = new char[8];
        int len;
        while (reader.read(controlChars) != -1) {
            if (controlChars[0] != 0) {
                long byteInLine = this.getUInt(controlChars);
                char[] stdout;
                if (byteInLine > 1024) {
                    stdout = new char[1024];
                } else {
                    stdout = new char[(int) byteInLine];
                }
                while (byteInLine > 0) {
                    len = reader.read(stdout);
                    byteInLine -= len;
                    if (len != -1) {
                        buffer.append(stdout, 0, len);
                    }
                }
            }
        }
    }

    /**
     * Check that content length less then Integer.MAX_VALUE.
     * Try to get content length from entity
     * If length less then zero return 4096
     *
     * @param entity HttpEntity for get capacity.
     * @return Capacity.
     */
    private int getCapacity(final HttpEntity entity) {
        Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
                "HTTP entity too large to be buffered in memory");
        int capacity = (int) entity.getContentLength();
        if (capacity < 0) {
            capacity = 4096;
        }
        return capacity;
    }

    /**
     * Try to get charset from content type.
     * If charset not set, try get default charset by mime type
     * If not set return ISO-8859-1
     *
     * @param contentType Content type.
     * @return Charset.
     */
    private Charset getCharset(final ContentType contentType) {
        Charset charset = null;
        if (contentType != null) {
            charset = contentType.getCharset();
            if (charset == null) {
                ContentType defaultContentType =
                        ContentType.getByMimeType(contentType.getMimeType());
                if (defaultContentType != null) {
                    charset = defaultContentType.getCharset();
                } else {
                    charset = null;
                }
            }
        }

        if (charset == null) {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        return charset;
    }

    /**
     * Convert byte to long.
     *
     * @param byteForConvert Byte for convert.
     * @return Long Converted byte.
     */
    private long byteAsULong(final char byteForConvert) {
        return ((long) byteForConvert) & 0x00000000000000FFL;
    }

    /**
     * Convert byte from control byte to uint32.
     *
     * @param bytes Array of byte.
     * @return Long uint32.
     */
    private long getUInt(final char[] bytes) {
        return this.byteAsULong(bytes[7])
                | (this.byteAsULong(bytes[6]) << 8)
                | (this.byteAsULong(bytes[5]) << 16)
                | (this.byteAsULong(bytes[4]) << 24);
    }


}
