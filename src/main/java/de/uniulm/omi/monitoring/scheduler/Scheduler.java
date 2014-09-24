package de.uniulm.omi.monitoring.scheduler;

import de.uniulm.omi.monitoring.probes.api.Probe;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by daniel on 22.09.14.
 */
public class Scheduler {

    private final ScheduledExecutorService scheduledExecutorService;
    protected MetricReportingInterface metricReportingInterface;

    public Scheduler(int numOfWorkers, MetricReportingInterface metricReportingInterface) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(5);
        this.metricReportingInterface = metricReportingInterface;
    }

    public void registerProbe(Probe probe) {
        this.scheduledExecutorService.scheduleAtFixedRate(new ProbeWorker(probe, metricReportingInterface), 0, probe.getInterval().getPeriod(), probe.getInterval().getTimeUnit());
    }

    public void unregisterProbe(Probe probe) {

    }

}
