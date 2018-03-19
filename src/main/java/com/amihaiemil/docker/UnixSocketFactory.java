package com.amihaiemil.docker;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;
import org.apache.http.HttpHost;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

/**
 * Provides unix sockets connecting to a given unix socket file.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ParameterNumber (100 lines)
 */
public final class UnixSocketFactory implements ConnectionSocketFactory {
    /**
     * File pointing to the unix socket.
     */
    private final File unixSocket;

    /**
     * Ctor.
     * @param unixSocket File pointing to the unix socket.
     */
    public UnixSocketFactory(final File unixSocket) {
        this.unixSocket = unixSocket;
    }

    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        return UnixSocketChannel.open().socket();
    }

    @Override
    public Socket connectSocket(final int connectTimeout, final Socket socket,
        final HttpHost host, final InetSocketAddress remoteAddress,
        final InetSocketAddress localAddress, final HttpContext context)
        throws IOException {
        socket.setSoTimeout(connectTimeout);
        socket.getChannel().connect(
            new UnixSocketAddress(this.unixSocket)
        );
        return socket;
    }
}
