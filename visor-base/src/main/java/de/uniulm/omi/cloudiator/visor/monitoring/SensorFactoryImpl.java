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

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import de.uniulm.omi.cloudiator.visor.reporting.TelnetReporting;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 15.01.15.
 */
public class SensorFactoryImpl implements SensorFactory {

    private final ReportingInterface<Metric> buffer;

    @Inject
    public SensorFactoryImpl(@TelnetReporting ReportingInterface<Metric> reportingInterface) {
        this.buffer = reportingInterface;
    }

    @Override public Sensor from(String className)
        throws SensorNotFoundException, SensorInitializationException {
        checkNotNull(className);
        checkArgument(!className.isEmpty());
        return this.loadAndInitializeSensor(className);
    }

    protected Sensor loadAndInitializeSensor(String className)
        throws SensorNotFoundException, SensorInitializationException {
        try {
            Sensor sensor = (Sensor) Class.forName(className).newInstance();
            sensor.setBuffer(buffer);
            sensor.init();
            return sensor;
        } catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new SensorNotFoundException("Could not load sensor with name " + className, e);
        }
    }

}
