package de.uniulm.omi.monitoring.reporting.impl;

import de.uniulm.omi.monitoring.metric.Metric;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;

import java.util.concurrent.BlockingQueue;

public class MetricReportingWorker extends QueueWorker<Metric> {

    public MetricReportingWorker(BlockingQueue<Metric> queue,
                                 MetricReportingInterface metricReportingInterface) {
        super(queue);
        this.metricReportingInterface = metricReportingInterface;
    }

    private MetricReportingInterface metricReportingInterface;

    @Override
    protected void consume(Metric item) {
        this.metricReportingInterface.report(item);
    }

}
