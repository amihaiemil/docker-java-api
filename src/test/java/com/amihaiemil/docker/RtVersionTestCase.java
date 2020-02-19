package com.amihaiemil.docker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RtVersion}.
 * @author Michael Lux (michi.lux@gmail.com)
 * @since 0.0.11
 */
public class RtVersionTestCase {
    /**
     * Must return the same number of images as there are elements in the
     * json array returned by the service.
     * @throws IOException On I/O error.
     */
    @Test
    public final void queryDockerVersion() throws IOException {
        HttpClient client = mock(HttpClient.class);
        when(client.execute(any(HttpGet.class), any(ResponseHandler.class)))
            .thenAnswer(invocation -> {
                HttpResponse response = mock(HttpResponse.class);
                HttpEntity entity = mock(HttpEntity.class);
                when(response.getEntity()).thenReturn(entity);
                when(entity.getContent()).thenReturn(
                    getClass().getClassLoader()
                        .getResourceAsStream("version.json"));
                StatusLine statusLine = mock(StatusLine.class);
                when(response.getStatusLine()).thenReturn(statusLine);
                when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
                return ((ReadJsonObject) invocation.getArguments()[1])
                    .handleResponse(response);
            });
        Docker docker = new LocalDocker(client, "v1.35");
        Version version = docker.version();
        assertEquals("19.03.3", version.version());
        assertEquals("Docker Engine - Community", version.platformName());
        assertEquals("1.40", version.apiVersion());
        assertEquals("1.12", version.minApiVersion());
        assertEquals("linux", version.os());
        assertEquals("amd64", version.arch());
        assertTrue(version.experimental());
    }

}
