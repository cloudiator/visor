package de.uniulm.omi.monitoring.probes;

import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 22.09.14.
 */
public class Interval {

    protected long period;

    protected TimeUnit timeUnit;

    public Interval(long period, TimeUnit timeUnit) {
        this.period = period;
        this.timeUnit = timeUnit;
    }

    public long getPeriod() {
        return period;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
