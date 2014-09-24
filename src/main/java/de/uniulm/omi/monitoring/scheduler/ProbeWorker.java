package de.uniulm.omi.monitoring.scheduler;

import com.sun.media.jfxmedia.logging.Logger;
import de.uniulm.omi.monitoring.metric.MetricBuilder;
import de.uniulm.omi.monitoring.probes.api.Probe;
import de.uniulm.omi.monitoring.probes.impl.MetricNotAvailableException;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;
import org.apache.logging.log4j.LogManager;

/**
 * Created by daniel on 22.09.14.
 */
public class ProbeWorker implements Runnable{

    protected Probe probe;
    protected MetricReportingInterface metricReportingInterface;

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ProbeWorker.class);

    public ProbeWorker(Probe probe, MetricReportingInterface metricReportingInterface) {
        this.probe = probe;
        this.metricReportingInterface = metricReportingInterface;
    }

    @Override
    public void run() {
        try {
            this.metricReportingInterface.report(MetricBuilder.getInstance().newMetric(probe.getMetricName(), probe.getMetricValue()));
        } catch (MetricNotAvailableException e) {
            logger.error(String.format("Could not retrieve metric %s",probe.getMetricName()));
        }
    }
}
