/*
 * Copyright (c) 2014-2016 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 11.07.16.
 */
public class MeasurementDifference {

  private final Measurement<?> old;
  private final Measurement<?> current;

  private MeasurementDifference(Measurement<?> old, Measurement<?> current) {
    checkArgument(old.getTimestamp() < current.getTimestamp(), String.format(
        "Timestamp of old measurement (%s) must be less than the timestamp (%s) of the current measurement.",
        old.getTimestamp(), current.getTimestamp()));
    this.old = old;
    this.current = current;
  }

  public static MeasurementDifference of(Measurement<?> old, Measurement<?> current) {
    return new MeasurementDifference(old, current);
  }

  BigDecimal difference() {
    return new BigDecimal(current.getValue().toString())
        .subtract(new BigDecimal(old.getValue().toString()));
  }

  public BigDecimal timeDifference(long timeScale, TimeUnit timeUnit) {

    checkArgument(timeScale > 0, "Timescale must be larger than 0");

    BigDecimal valueDifference = difference();
    if (BigDecimal.ZERO.compareTo(valueDifference) == 0) {
      return BigDecimal.ZERO;
    }

    BigDecimal timeBetweenMeasurementsInMillis =
        BigDecimal.valueOf(current.getTimestamp() - old.getTimestamp());

    checkState(timeBetweenMeasurementsInMillis.compareTo(BigDecimal.ZERO) > 0,
        "Time between measurements is not larger than 0");

    BigDecimal timeScaleBetweenMeasurementsInMillis =
        BigDecimal.valueOf(timeUnit.toMillis(timeScale));

    BigDecimal weight = timeBetweenMeasurementsInMillis.setScale(10, BigDecimal.ROUND_CEILING)
        .divide(timeScaleBetweenMeasurementsInMillis, BigDecimal.ROUND_CEILING);

    return valueDifference.setScale(10, BigDecimal.ROUND_CEILING)
        .divide(weight, BigDecimal.ROUND_CEILING);
  }


}
