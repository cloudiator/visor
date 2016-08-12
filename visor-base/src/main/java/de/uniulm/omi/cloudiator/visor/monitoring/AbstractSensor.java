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

import com.google.common.base.MoreObjects;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;

import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Daniel Baur
 */
public abstract class AbstractSensor<E> implements Sensor {

    private volatile boolean isInitialized = false;
    private volatile SensorConfiguration sensorConfiguration;

    @Override
    public final void init(MonitorContext monitorContext, SensorConfiguration sensorConfiguration)
        throws SensorInitializationException {
        this.initialize(monitorContext, sensorConfiguration);
        this.sensorConfiguration = sensorConfiguration;
        this.isInitialized = true;
    }

    @Override public final Set<Measurement<E>> getMeasurements()
        throws MeasurementNotAvailableException {
        checkState(isInitialized, "Measurement method was called before initialization.");
        Set<Measurement<E>> measurements = measureSet();
        if (measurements.isEmpty()) {
            Measurement<E> single = measureSingle();
            if (single != null) {
                return Collections.singleton(single);
            }
            throw new MeasurementNotAvailableException(
                this + "does not implement measureSingle or measureSet");
        }
        return measurements;
    }

    @Override public final SensorConfiguration sensorConfiguration() {
        if (!isInitialized) {
            throw new IllegalStateException("sensor not initialized yet.");
        }
        return sensorConfiguration;
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
    protected Measurement<E> measureSingle() throws MeasurementNotAvailableException {
        return null;
    }

    protected Set<Measurement<E>> measureSet() throws MeasurementNotAvailableException {
        return Collections.emptySet();
    }

    /**
     * Provides a type safe measurement builder.
     *
     * @return a measurement builder
     */
    protected final MeasurementBuilder<E> measurementBuilder(Class<E> eClass) {
        return MeasurementBuilder.newBuilder(eClass);
    }

    /**
     * Provides an object base measurement builder.
     *
     * @return a measurement builder
     */
    protected final MeasurementBuilder<?> measurementBuilder() {
        return MeasurementBuilder.newBuilder();
    }

    @Override public final String toString() {
        return MoreObjects.toStringHelper(this).toString();
    }
}
