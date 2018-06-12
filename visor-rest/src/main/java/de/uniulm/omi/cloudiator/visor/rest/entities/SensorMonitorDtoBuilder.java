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

package de.uniulm.omi.cloudiator.visor.rest.entities;

import de.uniulm.omi.cloudiator.visor.monitoring.DataSink;
import de.uniulm.omi.cloudiator.visor.monitoring.Interval;
import java.util.Collection;
import java.util.Map;

public class SensorMonitorDtoBuilder {

  private String uuid;
  private String metricName;
  private String componentId;
  private Map<String, String> monitorContext;
  private String sensorClassName;
  private Interval interval;
  private Map<String, String> sensorConfiguration;
  private Collection<DataSink> dataSinks;

  public SensorMonitorDtoBuilder uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public SensorMonitorDtoBuilder metricName(String metricName) {
    this.metricName = metricName;
    return this;
  }

  public SensorMonitorDtoBuilder componentId(String componentId) {
    this.componentId = componentId;
    return this;
  }

  public SensorMonitorDtoBuilder monitorContext(Map<String, String> monitorContext) {
    this.monitorContext = monitorContext;
    return this;
  }

  public SensorMonitorDtoBuilder sensorConfiguration(Map<String, String> sensorConfiguration) {
    this.sensorConfiguration = sensorConfiguration;
    return this;
  }

  public SensorMonitorDtoBuilder sensorClassName(String sensorClassName) {
    this.sensorClassName = sensorClassName;
    return this;
  }

  public SensorMonitorDtoBuilder interval(Interval interval) {
    this.interval = interval;
    return this;
  }

  public SensorMonitorDtoBuilder dataSinks(Collection<DataSink> dataSinks) {
    this.dataSinks = dataSinks;
    return this;
  }

  public SensorMonitorDto build() {
    return new SensorMonitorDto(uuid, metricName, componentId, sensorConfiguration,
        monitorContext, sensorClassName, interval, dataSinks);
  }
}
