/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.monitoring;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import java.util.concurrent.TimeUnit;

/**
 * The interval class.
 * <p/>
 * Represents an interval consisting of a timeunit and a period.
 */
public class DefaultInterval implements Interval {

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
   * <p/>
   * Use {@link Intervals} instead.
   *
   * @param period the period of the interval, must be larger then 0.
   * @param timeUnit the time unit of the interval.
   */
  DefaultInterval(long period, TimeUnit timeUnit) {
    checkArgument(period > 0, "The period must be > 0");
    checkNotNull(timeUnit, "The time unit must not be null.");
    this.period = period;
    this.timeUnit = timeUnit;
  }

  DefaultInterval(long period, String timeUnit) {
    this(period, TimeUnit.valueOf(timeUnit));
  }

  /**
   * Empty constructor for Deserialization.
   * <p/>
   * Use {@link Intervals} instead.
   */
  private DefaultInterval() {
  }

  /**
   * Getter for the period.
   *
   * @return the period of the interval.
   */
  @Override
  public long getPeriod() {
    return period;
  }

  /**
   * Getter for the timeunit.
   *
   * @return the timeunit of the interval.
   */
  @Override
  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("period", period).add("timeUnit", timeUnit)
        .toString();
  }
}
