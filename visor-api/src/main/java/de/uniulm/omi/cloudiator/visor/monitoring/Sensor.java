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

public interface Sensor {

    /**
     * Initializes the sensor.
     * <p>
     * This e.g. allows the sensor to install dependencies for the operating
     * system, or allows the sensor to configure itself for the current
     * environment.
     *
     * @throws SensorInitializationException for problems during the initialization
     */
    public void init() throws SensorInitializationException;

    /**
     * Sets the monitor context for the sensor.
     * <p>
     * Tells the sensor the context in which it is running.
     *
     * @param monitorContext context of the sensor.
     *
     * @throws InvalidMonitorContextException if the monitor context is not valid.
     */
    public void setMonitorContext(MonitorContext monitorContext) throws InvalidMonitorContextException;

    /**
     * Called to retrieve a measurement from this probe.
     *
     * @return the current measurement for this probe.
     *
     * @throws MeasurementNotAvailableException
     */
    public Measurement getMeasurement() throws MeasurementNotAvailableException;
}
