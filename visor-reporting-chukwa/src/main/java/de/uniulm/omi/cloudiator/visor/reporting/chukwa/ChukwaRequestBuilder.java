package de.uniulm.omi.cloudiator.visor.reporting.chukwa;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.base.MoreObjects;

public class ChukwaRequestBuilder {
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

    public ChukwaRequest build() {
        return new ChukwaRequestImpl(numberOfEvents, protocolVersion, sequenceId,
            source, tags, streamName, dataType, debuggingInfo, numberOfRecords, data);
    }
    
    static class ChukwaRequestImpl implements ChukwaRequest {

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

        public ChukwaRequestImpl(int numberOfEvents, int protocolVersion, long sequenceId,
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

        @Override public String toString() {
            return MoreObjects.toStringHelper(this).add("numberOfEvents", numberOfEvents)
                .add("protocolVersion", protocolVersion).add("sequenceId", sequenceId)
                .add("source", source).add("tags", tags).add("streamName", streamName)
                .add("dataType", dataType).add("debuggingInfo", debuggingInfo)
                .add("numberOfRecords", numberOfRecords).add("dataLength", data.length).toString();
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
                dataOutputStream.writeInt(data.length - 1);
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