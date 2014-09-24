package de.uniulm.omi.monitoring.probes.api;

import de.uniulm.omi.monitoring.probes.Interval;
import de.uniulm.omi.monitoring.probes.impl.MetricNotAvailableException;

/**
 * Created by daniel on 22.09.14.
 */
public interface Probe {

    public Interval getInterval();

    public String getMetricName();

    public Object getMetricValue() throws MetricNotAvailableException;
}
