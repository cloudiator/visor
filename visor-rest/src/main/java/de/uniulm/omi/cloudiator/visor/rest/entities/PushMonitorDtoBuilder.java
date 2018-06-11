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
import java.util.Map;

public class PushMonitorDtoBuilder {

  private String uuid;
  private String metricName;
  private String componentId;
  private Map<String, String> monitorContext;
  private int port;
  private DataSink dataSink;

  public PushMonitorDtoBuilder uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public PushMonitorDtoBuilder metricName(String metricName) {
    this.metricName = metricName;
    return this;
  }

  public PushMonitorDtoBuilder componentId(String componentId) {
    this.componentId = componentId;
    return this;
  }

  public PushMonitorDtoBuilder monitorContext(Map<String, String> monitorContext) {
    this.monitorContext = monitorContext;
    return this;
  }

  public PushMonitorDtoBuilder port(int port) {
    this.port = port;
    return this;
  }

  public PushMonitorDtoBuilder dataSink(DataSink dataSink) {
    this.dataSink = dataSink;
    return this;
  }

  public PushMonitorDto build() {
    return new PushMonitorDto(uuid, metricName, componentId, monitorContext, port, dataSink);
  }
}
