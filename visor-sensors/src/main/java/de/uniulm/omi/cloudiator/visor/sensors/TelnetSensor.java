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

package de.uniulm.omi.cloudiator.visor.sensors;

import de.uniulm.omi.cloudiator.visor.monitoring.*;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by daniel on 24.11.15.
 */
public class TelnetSensor extends AbstractSensor {

    private LinkedBlockingQueue<Metric> buffer;

    @Override public void setBuffer(ReportingInterface<Metric> reportingInterface) {
        this.buffer = ((TelnetSensorReportingInterface) reportingInterface).getBuffer();
    }

    @Override protected Measurement getMeasurement(MonitorContext monitorContext)
        throws MeasurementNotAvailableException {
        try {
            final Metric take = buffer.take();
            return new MeasurementImpl(take.getTimestamp(), take.getValue());
        } catch (InterruptedException e) {
            throw new MeasurementNotAvailableException("Telnet sensor got interrupted");
        }

    }

}
