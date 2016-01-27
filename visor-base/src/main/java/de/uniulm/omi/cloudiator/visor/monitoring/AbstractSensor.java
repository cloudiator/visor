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

package de.uniulm.omi.cloudiator.visor.monitoring;

import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Daniel Baur
 */
public abstract class AbstractSensor implements Sensor {

    private boolean isInitialized = false;

    @Override
    public final void init(MonitorContext monitorContext, SensorConfiguration sensorConfiguration)
        throws SensorInitializationException {
        this.initialize(monitorContext, sensorConfiguration);
        this.isInitialized = true;
    }

    @Override public final Measurement getMeasurement() throws MeasurementNotAvailableException {
        checkState(isInitialized, "Measurement method was called before initialization.");
        return measure();
    }

    /**
     * Provides the possibility to initialize the sensor.
     *
     * @param monitorContext      the context of the sensor
     * @param sensorConfiguration the configuration of the sensor
     * @throws SensorInitializationException if it was not possible to init the sensor
     */
    protected void initialize(MonitorContext monitorContext,
        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        // intentionally left empty
    }

    /**
     * Returns a single measurement object.
     *
     * @return a measurement taken by this sensor.
     * @throws MeasurementNotAvailableException
     */
    protected abstract Measurement measure() throws MeasurementNotAvailableException;

    /**
     * Provides a new measurement builder.
     *
     * @return a measurement builder
     */
    protected final MeasurementBuilder measureMentBuilder() {
        return MeasurementBuilder.newBuilder();
    }

    @Override public final String toString() {
        return this.getClass().getCanonicalName();
    }
}
