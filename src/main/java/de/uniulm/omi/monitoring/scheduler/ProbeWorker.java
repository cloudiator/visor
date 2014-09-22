package de.uniulm.omi.monitoring.scheduler;

import de.uniulm.omi.monitoring.metric.MetricBuilder;
import de.uniulm.omi.monitoring.probes.Probe;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;

/**
 * Created by daniel on 22.09.14.
 */
public class ProbeWorker implements Runnable{

    protected Probe probe;
    protected MetricReportingInterface metricReportingInterface;

    public ProbeWorker(Probe probe, MetricReportingInterface metricReportingInterface) {
        this.probe = probe;
        this.metricReportingInterface = metricReportingInterface;
    }

    @Override
    public void run() {
        this.metricReportingInterface.report(MetricBuilder.getInstance().newMetric(probe.getMetricName(), probe.getMetricValue()));
    }
}
