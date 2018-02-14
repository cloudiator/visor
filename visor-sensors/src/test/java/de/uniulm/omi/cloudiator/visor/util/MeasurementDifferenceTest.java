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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MeasurementBuilder;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Created by daniel on 11.07.16.
 */
public class MeasurementDifferenceTest {

  private static MeasurementDifference getValidDifferenceForValues(BigDecimal oldValue,
      BigDecimal newValue, long timeDifference) {

    long initialTime = 0L;
    long secondTime = initialTime + timeDifference;

    return MeasurementDifference
        .of(MeasurementBuilder.newBuilder(BigDecimal.class).timestamp(initialTime)
                .value(oldValue).build(),
            MeasurementBuilder.newBuilder(BigDecimal.class).timestamp(secondTime)
                .value(newValue).build());
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkTimestampOfCurrentMeasurementGTEOldMeasurement() {
    Measurement<Double> old =
        MeasurementBuilder.newBuilder(Double.class).timestamp(1L).value(1d).build();
    Measurement<Double> current =
        MeasurementBuilder.newBuilder(Double.class).timestamp(0L).value(1d).build();
    MeasurementDifference.of(old, current);
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkTimestampOfCurrentMeasurementEqualOldMeasurement() {
    final BigDecimal old = BigDecimal.ZERO;
    final BigDecimal current = BigDecimal.ONE;
    assertThat(getValidDifferenceForValues(old, current, 0).difference(),
        equalTo(BigDecimal.ZERO));
  }

  @Test
  public void checkPositiveDifference() {
    final BigDecimal old = BigDecimal.ZERO;
    final BigDecimal current = BigDecimal.valueOf(5d);
    assertThat(getValidDifferenceForValues(old, current, 1).difference(),
        equalTo(BigDecimal.valueOf(5d)));
  }

  @Test
  public void checkNegativeDifference() {
    final BigDecimal old = BigDecimal.valueOf(5d);
    final BigDecimal current = BigDecimal.ZERO;
    assertThat(getValidDifferenceForValues(old, current, 1).difference(),
        equalTo(BigDecimal.valueOf(-5d)));
  }

  @Test
  public void checkTimeDifferenceSameTimeUnit() {
    final BigDecimal old = BigDecimal.ZERO;
    final BigDecimal current = BigDecimal.valueOf(5d);

    assertThat(getValidDifferenceForValues(old, current, 1000)
            .timeDifference(1000, TimeUnit.MILLISECONDS).compareTo(BigDecimal.valueOf(5d)),
        equalTo(0));
  }

  @Test
  public void checkTimeDifferenceDifferentTimeUnit() {
    final BigDecimal old = BigDecimal.ZERO;
    final BigDecimal current = BigDecimal.valueOf(5d);

    assertThat(
        getValidDifferenceForValues(old, current, 1000).timeDifference(1, TimeUnit.SECONDS)
            .compareTo(BigDecimal.valueOf(5d)), equalTo(0));
  }

  @Test
  public void checkTimeDifferenceFloatSameTimeUnit() {
    final BigDecimal old = BigDecimal.ZERO;
    final BigDecimal current = BigDecimal.valueOf(2.5d);
    assertThat(
        getValidDifferenceForValues(old, current, 1000).timeDifference(1, TimeUnit.SECONDS)
            .compareTo(BigDecimal.valueOf(2.5d)), equalTo(0));
  }

  @Test
  public void checkTimeDifferenceFloatDifferentTimeUnit() {
    final BigDecimal old = BigDecimal.ZERO;
    final BigDecimal current = BigDecimal.valueOf(5d);

    assertThat(getValidDifferenceForValues(old, current, 1000)
            .timeDifference(500, TimeUnit.MILLISECONDS).compareTo(BigDecimal.valueOf(2.5d)),
        equalTo(0));
  }

  @Test
  public void checkTimeDifferenceOtherValue() {
    final BigDecimal old = BigDecimal.valueOf(8957.546d);
    final BigDecimal current = BigDecimal.valueOf(9587.567d);
    final BigDecimal difference = getValidDifferenceForValues(old, current, 1000)
        .timeDifference(500, TimeUnit.MILLISECONDS);

    assertThat(difference.compareTo(BigDecimal.valueOf(315.0105d)), equalTo(0));
  }

  @Test
  public void checkTimeDifferenceSmallValue() {
    final BigDecimal old = BigDecimal.valueOf(9000);
    final BigDecimal current = BigDecimal.valueOf(9000.001);
    final BigDecimal difference =
        getValidDifferenceForValues(old, current, 10000).timeDifference(1, TimeUnit.SECONDS);

    assertThat(difference.compareTo(BigDecimal.valueOf(0.0001d)), equalTo(0));
  }

  @Test
  public void checkTimeDifferenceNoValueButLongTime() {
    final BigDecimal old = BigDecimal.valueOf(1000);
    final BigDecimal current = BigDecimal.valueOf(1000);
    final BigDecimal difference =
        getValidDifferenceForValues(old, current, 10000).timeDifference(1, TimeUnit.SECONDS);

    assertThat(difference.compareTo(BigDecimal.ZERO), equalTo(0));
  }

  @Test
  public void checkNonTerminatingDecimalExpansion() {
    final BigDecimal old = BigDecimal.ZERO;
    final BigDecimal current = BigDecimal.ONE;
    final BigDecimal difference =
        getValidDifferenceForValues(old, current, 3000).timeDifference(1, TimeUnit.SECONDS);
    assertThat(difference.compareTo(BigDecimal.valueOf(0.3333333334d)), equalTo(0));
  }

}
