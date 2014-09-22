package de.uniulm.omi.monitoring.reporting.impl;

import de.uniulm.omi.monitoring.metric.Metric;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;

/**
 * Created by daniel on 22.09.14.
 */
public class CommandLine implements MetricReportingInterface {

    @Override
    public void report(Metric metric) {
        System.out.println(String.format("put %s %s %s", metric.getName(),
                metric.getValue(), metric.getTimestamp()));
    }
}
