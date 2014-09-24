package de.uniulm.omi.monitoring.probes.impl;

import com.sun.management.OperatingSystemMXBean;
import de.uniulm.omi.monitoring.probes.Interval;
import de.uniulm.omi.monitoring.probes.api.Probe;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 24.09.14.
 */
public class MemoryUsageProbe implements Probe{

    @Override
    public Interval getInterval() {
        return new Interval(20, TimeUnit.SECONDS);
    }

    @Override
    public String getMetricName() {
        return "memory_usage_percentage";
    }

    @Override
    public Double getMetricValue() throws MetricNotAvailableException {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);

        //memory usage
        double totalPhysicalMemory = osBean.getTotalPhysicalMemorySize();
        double freePhysicalMemory = osBean.getFreePhysicalMemorySize();

        if(totalPhysicalMemory < 0 || freePhysicalMemory < 0) {
            throw new MetricNotAvailableException("Received negative value for total or free physical memory size");
        }

        double usedMemory = 100 - ((freePhysicalMemory/totalPhysicalMemory)*100);

        return usedMemory;
    }

}
