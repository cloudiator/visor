package de.uniulm.omi.monitoring.metric;

import java.util.List;

public class Metric {


	protected String name;
	
	protected String value;
	
	protected long timestamp;

    protected String applicationName;

    protected String Ip;



    public Metric(String name, String value, long timestamp, String applicationName, String Ip) {
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getIp() {
        return Ip;
    }

    public String getValue() {
        return value;
    }



    public long getTimestamp() {
        return timestamp;
    }

}
