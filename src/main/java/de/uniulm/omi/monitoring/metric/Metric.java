package de.uniulm.omi.monitoring.metric;

import java.util.List;

public class Metric {

	protected String name;
	
	protected String value;
	
	protected long timestamp;

    protected String ip;

    public Metric(String name, String value, long timestamp, String ip) {
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }



    public String getIp() {
        return ip;
    }

    public String getValue() {
        return value;
    }



    public long getTimestamp() {
        return timestamp;
    }

}
