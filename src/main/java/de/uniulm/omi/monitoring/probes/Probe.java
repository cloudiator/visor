package de.uniulm.omi.monitoring.probes;

/**
 * Created by daniel on 22.09.14.
 */
public interface Probe {

    public Interval getInterval();

    public String getMetricName();

    public String getMetricValue();
}
