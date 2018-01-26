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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import de.uniulm.omi.cloudiator.visor.exceptions.SensorCreationException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;

/**
 * Created by daniel on 15.01.15.
 */
public class SensorFactoryImpl implements SensorFactory {

  @Override
  public Sensor from(String className, SensorConfiguration sensorConfiguration,
      MonitorContext monitorContext)
      throws SensorCreationException, SensorInitializationException {
    checkNotNull(className);
    checkArgument(!className.isEmpty());
    return this.loadAndInitializeSensor(className, sensorConfiguration, monitorContext);
  }

  protected Sensor loadAndInitializeSensor(String className,
      SensorConfiguration sensorConfiguration, MonitorContext monitorContext)
      throws SensorCreationException, SensorInitializationException {
    try {
      Sensor sensor = (Sensor) Class.forName(className).newInstance();
      sensor.init(monitorContext, sensorConfiguration);
      return sensor;
    } catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new SensorCreationException("Could not create sensor with className " + className,
          e);
    }
  }

}
