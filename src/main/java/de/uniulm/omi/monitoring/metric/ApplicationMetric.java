package de.uniulm.omi.monitoring.metric;

/**
 * Created by daniel on 22.09.14.
 */
public class ApplicationMetric extends Metric {

    public ApplicationMetric(String name, Object value, long timestamp, String applicationName, String Ip) {
        super(name, value, timestamp, Ip);
        this.applicationName = applicationName;
    }

    protected String applicationName;

    public String getApplicationName() {
        return applicationName;
    }

}
