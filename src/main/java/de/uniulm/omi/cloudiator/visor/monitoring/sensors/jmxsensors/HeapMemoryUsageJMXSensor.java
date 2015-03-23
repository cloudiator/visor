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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import de.uniulm.omi.cloudiator.visor.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.monitoring.api.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.api.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.sensors.AbstractSensor;

/**
 * 
 * @author zarioha
 * A probe for measuring the amount of the heap used memory in bytes
 */
public class HeapMemoryUsageJMXSensor extends AbstractSensor {
	
	private  MemoryMXBean memoryBean;
    @Override
    protected Measurement getMeasurement(MonitorContext monitorContext) throws
        MeasurementNotAvailableException {
        long heapMemoryUsed = memoryBean.getHeapMemoryUsage().getUsed();
        return new MeasurementImpl(System.currentTimeMillis(), heapMemoryUsed);
    }
    
    @Override
    protected void initialize() throws SensorInitializationException {
    	super.initialize();
    	memoryBean = ManagementFactory.getMemoryMXBean();
    }
}
