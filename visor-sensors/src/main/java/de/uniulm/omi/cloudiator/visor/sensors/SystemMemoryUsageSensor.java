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

import com.sun.management.OperatingSystemMXBean;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.AbstractSensor;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;
import java.lang.management.ManagementFactory;

/**
 * The MemoryUsageProbe class. <p> Measures the current ly used memory by the operating system in
 * percentage.
 */
public class SystemMemoryUsageSensor extends AbstractSensor {

  private OperatingSystemMXBean osBean;

  @Override
  protected Measurement measureSingle() throws MeasurementNotAvailableException {
    //memory usage
    double totalPhysicalMemory = osBean.getTotalPhysicalMemorySize();
    double freePhysicalMemory = osBean.getFreePhysicalMemorySize();

    if (totalPhysicalMemory < 0 || freePhysicalMemory < 0) {
      throw new MeasurementNotAvailableException(
          "Received negative value for total or free physical memory size");
    }

    return measurementBuilder(Double.class).now()
        .value(100 - ((freePhysicalMemory / totalPhysicalMemory) * 100)).build();
  }

  @Override
  protected void initialize(MonitorContext monitorContext,
      SensorConfiguration sensorConfiguration) throws SensorInitializationException {
    super.initialize(monitorContext, sensorConfiguration);
    osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
  }
}
