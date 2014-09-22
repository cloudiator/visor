package de.uniulm.omi.monitoring.metric;

import de.uniulm.omi.monitoring.MonitoringAgent;
import de.uniulm.omi.monitoring.server.IllegalRequestException;

/**
 * Created by daniel on 22.09.14.
 */
public class MetricBuilder {

    /**
     * Singleton instance.
     */
    private static MetricBuilder instance = new MetricBuilder();

    /**
     * Private constructor for singleton pattern.
     */
    private MetricBuilder() {

    }

    /**
     * Returns instance of the metric builder.
     * Implementation of the singleton pattern.
     *
     * @return unique instance of metric builder.
     */
    public static MetricBuilder getInstance() {
        return instance;
    }

    public Metric newMetric(String request) throws IllegalRequestException {
        return this.fromRequest(request);
    }

    protected Metric fromRequest(String request) throws IllegalRequestException {
        // split the request at blanks
        String[] parts = request.split(" ");

        if(parts.length != 4) {
            throw new IllegalRequestException("Illegal request.");
        }

        String applicationName = parts[1];
        String metricName = parts[1];
        String value = parts[2];
        long timestamp = Long.valueOf(parts[3]);

        return new Metric(metricName, value, timestamp, applicationName, MonitoringAgent.localIp);

    }

}
