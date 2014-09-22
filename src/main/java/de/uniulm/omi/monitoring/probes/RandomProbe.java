package de.uniulm.omi.monitoring.probes;

import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 22.09.14.
 */
public class RandomProbe implements Probe{

    private final String name;

    public RandomProbe(String name) {
        this.name = name;
    }

    @Override
    public Interval getInterval() {
        return new Interval(1, TimeUnit.SECONDS);
    }

    @Override
    public String getMetricName() {
        return this.name;
    }

    @Override
    public String getMetricValue() {
        System.out.println(this.name);
        return "1000";
    }
}
