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
package com.amihaiemil.docker.mock;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jnr.enxio.channels.NativeSelectorProvider;
import jnr.unixsocket.UnixServerSocketChannel;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;

/**
 * A simple UNIX socket server.
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class UnixServer implements Closeable {
    /**
     * The UNIX socket file.
     */
    private final Path socketFile;
    /**
     * The unix socket channel.
     */
    private final UnixServerSocketChannel channel;
    /**
     * Selector.
     */
    private final Selector selector;
    /**
     * The service runs in a thread in this pool.
     */
    private final ExecutorService threadPool;
  
    /**
     * Ctor.
     * <p>
     * Eagerly opens a unix file socket created in a temp directory.
     * @param response The mock response to send back to the caller.
     * @throws IOException If an I/O error occurs.
     */
    public UnixServer(final Response response) throws IOException {
        this.socketFile = Files.createTempFile("", "");
        this.channel = UnixServerSocketChannel.open();
        this.channel.configureBlocking(false);
        this.channel.socket().bind(
            new UnixSocketAddress(this.socketFile.toFile())
        );
        this.selector = NativeSelectorProvider.getInstance().openSelector();
        this.channel.register(this.selector, SelectionKey.OP_READ);
        this.threadPool = Executors.newSingleThreadExecutor();
        this.threadPool.submit(new Service(this.selector, response));
    }
  
    /**
     * The UNIX socket file.
     * @return The unix socket file opened by this server.
     */
    public Path socketFile() {
        return this.socketFile;
    }
  
    @Override
    public void close() throws IOException {
        this.threadPool.shutdownNow();
        this.selector.close();
        this.channel.close();
        Files.delete(this.socketFile);
    }
  
    /**
     * Background thread that handles requests to the socket.
     */
    private static final class Service implements Callable<Void> {
        /**
         * Selector.
         */
        private final Selector selector;
        /**
         * Response to send back to caller.
         */
        private final Response response;
  
        /**
         * Ctor.
         * @param selector The selector to listen on.
         * @param response The response to send back to the client.
         */
        private Service(final Selector selector, final Response response) {
            this.selector = selector;
            this.response = response;
        }
  
        @Override
        public Void call() throws IOException {
            while (this.selector.isOpen()) {
                this.selector.select();
                final Iterator<SelectionKey> iter = this.selector.selectedKeys()
                    .iterator();
                while (iter.hasNext()) {
                    final SelectionKey key = iter.next();
                    iter.remove();
                    final UnixSocketChannel channel =
                        (UnixSocketChannel) key.channel();
                    channel.configureBlocking(false);
                    this.response.printTo(channel);
                }
            }
            return null;
        }
    }
}
