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

package de.uniulm.omi.cloudiator.visor.monitoring.sensors.jmxsensors;

import de.uniulm.omi.cloudiator.visor.monitoring.api.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.monitoring.api.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.sensors.AbstractSensor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * 
 * @author zarioha
 * A probe for measuring the peak live thread count since the Java virtual machine started
 */
public class PeakThreadCountJMXSensor extends AbstractSensor {
	
	private  ThreadMXBean threadBean;
    @Override
    protected Measurement getMeasurement(MonitorContext monitorContext) throws
        MeasurementNotAvailableException {
        long peakThreadCount = threadBean.getPeakThreadCount();
        return new MeasurementImpl(System.currentTimeMillis(), peakThreadCount);
    }
    
    @Override
    protected void initialize() throws SensorInitializationException {
    	super.initialize();
    	threadBean = ManagementFactory.getThreadMXBean();
    }
}
