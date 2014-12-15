/*
 *
 *  * Copyright (c) 2014 University of Ulm
 *  *
 *  * See the NOTICE file distributed with this work for additional information
 *  * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package de.uniulm.omi.monitoring.probes;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The interval class.
 * <p>
 * Represents an interval consisting of a timeunit and a period.
 */
public class Interval {

    /**
     * The period of the interval.
     */
    protected long period;

    /**
     * The timeunit of the interval.
     */
    protected TimeUnit timeUnit;

    /**
     * Constructor for the interval
     *
     * @param period   the period of the interval, must be larger then 0.
     * @param timeUnit the time unit of the interval.
     */
    public Interval(long period, TimeUnit timeUnit) {
        checkArgument(period > 0, "The period must be > 0");
        checkNotNull(timeUnit, "The time unit must not be null.");
        this.period = period;
        this.timeUnit = timeUnit;
    }

    /**
     * Getter for the period.
     *
     * @return the period of the interval.
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Getter for the timeunit.
     *
     * @return the timeunit of the interval.
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public String toString() {
        return String.format("Interval{period=%d, timeUnit=%s}", period, timeUnit);
    }
}
