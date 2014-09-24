package de.uniulm.omi.monitoring.probes.impl;

import de.uniulm.omi.monitoring.probes.Interval;
import de.uniulm.omi.monitoring.probes.api.Probe;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 22.09.14.
 */
public class CpuUsageProbe implements Probe {

    @Override
    public Interval getInterval() {
        return new Interval(20, TimeUnit.SECONDS);
    }

    @Override
    public String getMetricName() {
        return "cpu_usage_percentage";
    }

    @Override
    public Double getMetricValue() throws MetricNotAvailableException {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);

        double systemCpuLoad = osBean.getSystemCpuLoad();
        double systemCpuLoadPercentage = systemCpuLoad*100;

        if(systemCpuLoad < 0) {
            throw new MetricNotAvailableException("Received negative value");
        }

        return systemCpuLoadPercentage;
    }
}
