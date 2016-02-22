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
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 22.02.16.
 */
public class ChukwaReporter implements ReportingInterface<Metric> {

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
        ChukwaClient chukwaClient = new ChukwaClient(chukwaUri);
        ChukwaRequest chukwaRequest = ChukwaRequestBuilder.newBuilder().build();

        chukwaClient.post(chukwaRequest);

        try {
            chukwaClient.close();
        } catch (IOException ignored) {
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

        public void post(ChukwaRequest chukwaRequest) {
            httpClient.start();
            final HttpAsyncRequestProducer post = HttpAsyncMethods
                .createPost(uri, chukwaRequest.toByteArray(), ContentType.APPLICATION_OCTET_STREAM);
            final Future<Object> execute = httpClient.execute(post, null, null);
            try {
                execute.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        @Override public void close() throws IOException {
            httpClient.close();
        }
    }


    private static class ChukwaRequestBuilder {
        private int numberOfEvents;
        private int protocolVersion;
        private long sequenceId;
        private String source;
        private String tags;
        private String streamName;
        private String dataType;
        private String debuggingInfo;
        private int numberOfRecords;
        private String data;

        private ChukwaRequestBuilder() {
        }

        public static ChukwaRequestBuilder newBuilder() {
            return new ChukwaRequestBuilder();
        }

        public ChukwaRequestBuilder setNumberOfEvents(int numberOfEvents) {
            this.numberOfEvents = numberOfEvents;
            return this;
        }

        public ChukwaRequestBuilder setProtocolVersion(int protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public ChukwaRequestBuilder setSequenceId(long sequenceId) {
            this.sequenceId = sequenceId;
            return this;
        }

        public ChukwaRequestBuilder setSource(String source) {
            this.source = source;
            return this;
        }

        public ChukwaRequestBuilder setTags(String tags) {
            this.tags = tags;
            return this;
        }

        public ChukwaRequestBuilder setStreamName(String streamName) {
            this.streamName = streamName;
            return this;
        }

        public ChukwaRequestBuilder setDataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public ChukwaRequestBuilder setDebuggingInfo(String debuggingInfo) {
            this.debuggingInfo = debuggingInfo;
            return this;
        }

        public ChukwaRequestBuilder setNumberOfRecords(int numberOfRecords) {
            this.numberOfRecords = numberOfRecords;
            return this;
        }

        public ChukwaRequestBuilder setData(String data) {
            this.data = data;
            return this;
        }

        public ChukwaReporter.ChukwaRequest build() {
            return new ChukwaReporter.ChukwaRequest(numberOfEvents, protocolVersion, sequenceId,
                source, tags, streamName, dataType, debuggingInfo, numberOfRecords, data);
        }
    }


    private static class ChukwaRequest {

        private final int numberOfEvents;
        private final int protocolVersion;
        private final long sequenceId;
        private final String source;
        private final String tags;
        private final String streamName;
        private final String dataType;
        private final String debuggingInfo;
        private final int numberOfRecords;
        private final String data;

        public ChukwaRequest(int numberOfEvents, int protocolVersion, long sequenceId,
            String source, String tags, String streamName, String dataType, String debuggingInfo,
            int numberOfRecords, String data) {
            this.numberOfEvents = numberOfEvents;
            this.protocolVersion = protocolVersion;
            this.sequenceId = sequenceId;
            this.source = source;
            this.tags = tags;
            this.streamName = streamName;
            this.dataType = dataType;
            this.debuggingInfo = debuggingInfo;
            this.numberOfRecords = numberOfRecords;
            this.data = data;
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
                dataOutputStream.writeInt(data.getBytes().length);
                dataOutputStream.writeUTF(data);
                dataOutputStream.flush();

                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }



}
