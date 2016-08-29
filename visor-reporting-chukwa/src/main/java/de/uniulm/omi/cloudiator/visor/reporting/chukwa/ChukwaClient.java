package de.uniulm.omi.cloudiator.visor.reporting.chukwa;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.entity.NByteArrayEntity;

import de.uniulm.omi.cloudiator.visor.reporting.chukwa.ChukwaRequest;

class ChukwaClient implements Closeable {

    private final CloseableHttpAsyncClient httpClient;
    private final URI uri;

    public ChukwaClient(URI uri) {
        httpClient = HttpAsyncClients.createDefault();
        this.uri = uri;
    }

    public HttpResponse post(ChukwaRequest chukwaRequest) throws IOException {

        httpClient.start();

        byte[] payload = chukwaRequest.toByteArray();

        final HttpPost httpPost = new HttpPost(uri);
        final NByteArrayEntity nByteArrayEntity =
            new NByteArrayEntity(payload, ContentType.APPLICATION_OCTET_STREAM);
        httpPost.setEntity(nByteArrayEntity);
        final Future<HttpResponse> execute = httpClient.execute(httpPost, null);
        try {
            return execute.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException(e);
        }
    }

    @Override public void close() throws IOException {
        httpClient.close();
    }
}