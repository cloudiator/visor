/*
 * Copyright (c) 2014-2016 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.reporting.chukwa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.visor.exceptions.ConfigurationException;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 22.02.16.
 */
public class ChukwaReporter implements ReportingInterface<Metric> {

    private static final Logger LOGGER = LogManager.getLogger(ChukwaReporter.class);
    private static final int DEFAULT_PROTOCOL_VERSION = 1;

    private final URI chukwaUri;
    private final String vmID;

    @Inject
    public ChukwaReporter(@Named("chukwaUrl") String chuwkaUrl, @Named("chukwaVmId") String vmId) {
        checkNotNull(vmId);
        checkArgument(!vmId.isEmpty());
        this.vmID = vmId;
        try {
            this.chukwaUri = new URI(chuwkaUrl);
        } catch (URISyntaxException e) {
            throw new ConfigurationException(e);
        }
    }

    @Override public void report(Metric item) throws ReportingException {

        try (ChukwaClient chukwaClient = new ChukwaClient(chukwaUri)) {

            ChukwaRequest chukwaRequest = ChukwaRequestBuilder.newBuilder().numberOfEvents(1)
                .protocolVersion(DEFAULT_PROTOCOL_VERSION).sequenceId(1L).source("Visor").tags("")
                .streamName("Visor Monitoring Information").dataType("Visor").debuggingInfo("")
                .numberOfRecords(1).stringData(new MetricToChukwa(vmID).apply(item)).build();

            final HttpResponse response = chukwaClient.post(chukwaRequest);

            LOGGER.debug("Chukwa response " + response.getStatusLine().toString());

        } catch (IOException e) {
            throw new ReportingException(e);
        }
    }

    @Override public void report(Collection<Metric> items) throws ReportingException {
        for (Metric metric : items) {
            this.report(metric);
        }
    }

    private static class ChukwaClient implements Closeable {

        private final CloseableHttpAsyncClient httpClient;
        private final URI uri;

        public ChukwaClient(URI uri) {
            httpClient = HttpAsyncClients.createDefault();
            this.uri = uri;
        }

        public HttpResponse post(ChukwaRequest chukwaRequest) throws IOException {

            /**
             URL url = new URL(uri.toString());
             final HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
             urlConnection.setRequestMethod("POST");
             urlConnection.setRequestProperty("Content-Length",
             String.valueOf(chukwaRequest.toByteArray().length));
             urlConnection.setRequestProperty("Content-Type","application/octet-stream");
             urlConnection.setRequestProperty("Host","134.60.64.143:8080");
             urlConnection.setDoOutput(true);
             urlConnection.setUseCaches(false);

             DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
             wr.write(chukwaRequest.toByteArray());
             wr.close();

             //Get Response
             InputStream is = urlConnection.getInputStream();
             BufferedReader rd = new BufferedReader(new InputStreamReader(is));
             StringBuilder response = new StringBuilder();
             String line;
             while((line = rd.readLine()) != null) {
             response.append(line);
             response.append('\r');
             }
             rd.close();
             System.out.println(response.toString());

             urlConnection.disconnect();

             **/
            httpClient.start();

            byte[] payload = chukwaRequest.toByteArray();

            //final HttpAsyncRequestProducer post = HttpAsyncMethods
            //    .createPost(uri, payload, ContentType.APPLICATION_OCTET_STREAM);

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


    public static class ChukwaRequestBuilder {
        private int numberOfEvents;
        private int protocolVersion;
        private long sequenceId;
        private String source;
        private String tags;
        private String streamName;
        private String dataType;
        private String debuggingInfo;
        private int numberOfRecords;
        private byte[] data;

        private ChukwaRequestBuilder() {
        }

        public static ChukwaRequestBuilder newBuilder() {
            return new ChukwaRequestBuilder();
        }

        public ChukwaRequestBuilder numberOfEvents(int numberOfEvents) {
            this.numberOfEvents = numberOfEvents;
            return this;
        }

        public ChukwaRequestBuilder protocolVersion(int protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public ChukwaRequestBuilder sequenceId(long sequenceId) {
            this.sequenceId = sequenceId;
            return this;
        }

        public ChukwaRequestBuilder source(String source) {
            this.source = source;
            return this;
        }

        public ChukwaRequestBuilder tags(String tags) {
            this.tags = tags;
            return this;
        }

        public ChukwaRequestBuilder streamName(String streamName) {
            this.streamName = streamName;
            return this;
        }

        public ChukwaRequestBuilder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public ChukwaRequestBuilder debuggingInfo(String debuggingInfo) {
            this.debuggingInfo = debuggingInfo;
            return this;
        }

        public ChukwaRequestBuilder numberOfRecords(int numberOfRecords) {
            this.numberOfRecords = numberOfRecords;
            return this;
        }

        public ChukwaRequestBuilder stringData(String data) {
            this.data = data.getBytes(Charset.forName("UTF-8"));
            return this;
        }

        public ChukwaReporter.ChukwaRequest build() {
            return new ChukwaReporter.ChukwaRequest(numberOfEvents, protocolVersion, sequenceId,
                source, tags, streamName, dataType, debuggingInfo, numberOfRecords, data);
        }
    }


    public static class ChukwaRequest {

        private final int numberOfEvents;
        private final int protocolVersion;
        private final long sequenceId;
        private final String source;
        private final String tags;
        private final String streamName;
        private final String dataType;
        private final String debuggingInfo;
        private final int numberOfRecords;
        private final byte[] data;

        public ChukwaRequest(int numberOfEvents, int protocolVersion, long sequenceId,
            String source, String tags, String streamName, String dataType, String debuggingInfo,
            int numberOfRecords, byte[] data) {
            this.numberOfEvents = numberOfEvents;
            this.protocolVersion = protocolVersion;
            this.sequenceId = sequenceId;
            this.source = source;
            this.tags = tags;
            this.streamName = streamName;
            this.dataType = dataType;
            this.debuggingInfo = debuggingInfo;
            this.numberOfRecords = numberOfRecords;
            this.data = data.clone();
        }

        public byte[] toByteArray() {
            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

                dataOutputStream.writeInt(numberOfEvents);
                dataOutputStream.writeInt(protocolVersion);
                dataOutputStream.writeLong(sequenceId);
                dataOutputStream.writeUTF(source);
                dataOutputStream.writeUTF(tags);
                dataOutputStream.writeUTF(streamName);
                dataOutputStream.writeUTF(dataType);
                dataOutputStream.writeUTF(debuggingInfo);
                dataOutputStream.writeInt(numberOfRecords);
                dataOutputStream.writeInt(data.length -1);
                dataOutputStream.write(data);

                dataOutputStream.flush();
                byteArrayOutputStream.flush();

                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }



}
