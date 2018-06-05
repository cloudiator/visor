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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.MoreObjects;
import de.uniulm.omi.cloudiator.visor.exceptions.MonitorException;
import de.uniulm.omi.cloudiator.visor.monitoring.Monitor;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitoringService;
import java.util.Collections;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * Created by daniel on 26.10.15.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = PushMonitorDto.class, name = "PushMonitor"),
    @JsonSubTypes.Type(value = SensorMonitorDto.class, name = "SensorMonitor")})
public abstract class MonitorDto {

  @JsonProperty("type")
  private String type;
  @JsonIgnore
  private String uuid;
  @NotNull
  private String metricName;
  @NotNull
  private String componentId;
  private Map<String, String> monitorContext;

  protected MonitorDto() {

  }

  public MonitorDto(String type, String uuid, String metricName, String componentId,
      Map<String, String> monitorContext) {
    this.type = type;
    this.uuid = uuid;
    this.metricName = metricName;
    this.componentId = componentId;
    this.monitorContext = monitorContext;
  }

  public abstract Monitor start(String uuid, MonitoringService monitoringService)
      throws MonitorException;

  public String getMetricName() {
    return metricName;
  }

  public void setMetricName(String metricName) {
    this.metricName = metricName;
  }

  public Map<String, String> getMonitorContext() {
    if (monitorContext == null) {
      return Collections.emptyMap();
    }
    return monitorContext;
  }

  public void setMonitorContext(Map<String, String> monitorContext) {
    this.monitorContext = monitorContext;
  }

  public String getComponentId() {
    return componentId;
  }

  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }

  @JsonProperty("uuid")
  public String getUuid() {
    return uuid;
  }

  @JsonIgnore
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  protected MoreObjects.ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(this).add("uuid", uuid).add("metricName", metricName)
        .add("componentId", componentId).add("monitorContext", monitorContext);
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }
}
