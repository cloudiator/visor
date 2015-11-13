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

import com.google.common.base.Preconditions;
import de.uniulm.omi.cloudiator.visor.exceptions.InvalidMonitorContextException;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Daniel Baur
 */
public abstract class AbstractSensor implements Sensor {

    private MonitorContext monitorContext;
    private boolean isInitialized = false;


    @Override public void init() throws SensorInitializationException {
        this.initialize();
        this.isInitialized = true;
    }

    @Override public void setMonitorContext(MonitorContext monitorContext)
        throws InvalidMonitorContextException {
        Preconditions.checkNotNull(monitorContext);
        checkState(isInitialized);
        if (!validateMonitorContext(monitorContext)) {
            throw new InvalidMonitorContextException();
        }
        this.monitorContext = monitorContext;
    }

    @Override public Measurement getMeasurement() throws MeasurementNotAvailableException {
        checkState(isInitialized, "Measurement method was called before initialization.");
        checkNotNull(monitorContext != null,
            "Measurement method was called, before monitoring context was set.");
        return this.getMeasurement(this.monitorContext);
    }

    protected boolean validateMonitorContext(MonitorContext monitorContext) {
        return true;
    }

    protected void initialize() throws SensorInitializationException {
        // intentionally left empty
    }

    /**
     * Returns a single measurement object.
     *
     * @param monitorContext the context for the measurement.
     * @return a measurement taken by this sensor.
     * @throws MeasurementNotAvailableException
     */
    protected abstract Measurement getMeasurement(MonitorContext monitorContext)
        throws MeasurementNotAvailableException;

    @Override public String toString() {
        return this.getClass().getCanonicalName();
    }
}
