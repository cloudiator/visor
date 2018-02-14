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


import de.uniulm.omi.cloudiator.visor.exceptions.MonitorException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Created by daniel on 11.12.14.
 */
public interface MonitoringService {

  Monitor startMonitor(String uuid, String componentId, String metricName, String sensorClassName,
      Interval interval, Map<String, String> monitorContext,
      SensorConfiguration sensorConfiguration) throws MonitorException;

  Monitor startMonitor(String uuid, String componentId, String metricName,
      Map<String, String> monitorContext, @Nullable Integer port) throws MonitorException;

  Collection<Monitor> getMonitors();

  Optional<Monitor> getMonitor(String uuid);

  boolean isMonitoring(String uuid);

  void stopMonitor(String uuid);
}
