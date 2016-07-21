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

import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MeasurementBuilder;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by daniel on 11.07.16.
 */
public class MeasurementDifferenceTest {

    private static MeasurementDifference getValidDifferenceForValues(Double oldValue,
        Double newValue, long timeDifference) {
        return MeasurementDifference
            .of(MeasurementBuilder.newBuilder(Double.class).timestamp(0L).value(oldValue).build(),
                MeasurementBuilder.newBuilder(Double.class).timestamp(timeDifference)
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

    @Test public void checkNoDifference() {
        final Double old = 0d;
        final Double current = 0d;

        assertThat(getValidDifferenceForValues(old, current, 1).difference(), equalTo(0d));
    }

    @Test public void checkPositiveDifference() {
        final Double old = 0d;
        final Double current = 5d;
        assertThat(getValidDifferenceForValues(old, current, 1).difference(), equalTo(5d));
    }

    @Test public void checkNegativeDifference() {
        final Double old = 5d;
        final Double current = 0d;
        assertThat(getValidDifferenceForValues(old, current, 1).difference(), equalTo(-5d));
    }

    @Test public void checkTimeDifferenceSameTimeUnit() {
        final Double old = 0d;
        final Double current = 5d;
        assertThat(getValidDifferenceForValues(old, current, 1000)
            .timeDifference(1000, TimeUnit.MILLISECONDS), equalTo(5d));
    }

    @Test public void checkTimeDifferenceDifferentTimeUnit() {
        final Double old = 0d;
        final Double current = 5d;
        assertThat(
            getValidDifferenceForValues(old, current, 1000).timeDifference(1, TimeUnit.SECONDS),
            equalTo(5d));
    }

    @Test public void checkTimeDifferenceFloatSameTimeUnit() {
        final Double old = 0d;
        final Double current = 5d;
        assertThat(getValidDifferenceForValues(old, current, 1000)
            .timeDifference(500, TimeUnit.MILLISECONDS), equalTo(2.5d));
    }

    @Test public void checkTimeDifferenceFloatDifferentTimeUnit() {
        final Double old = 0d;
        final Double current = 2.5d;
        assertThat(
            getValidDifferenceForValues(old, current, 1000).timeDifference(1, TimeUnit.SECONDS),
            equalTo(2.5d));
    }

}
