package de.uniulm.omi.monitoring;

import java.util.List;

public class Metric {

	
	protected String name;
	
	protected String value;
	
	protected long timestamp;

    protected List<Tag> tags;

    public Metric(String name, String value, long timestamp) {
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
    }

    public Metric(String name, String value, long timestamp, List<Tag> tags) {
        this(name, value, timestamp);
        this.tags = tags;
    }

    public Metric(String name, String value) {
        this.name = name;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public Metric(String name, String value, List<Tag> tags) {
        this(name, value);
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
