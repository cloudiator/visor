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

import de.uniulm.omi.cloudiator.visor.server.Server;

/**
 * Created by daniel on 11.11.15.
 */
public class PushMonitorImpl implements PushMonitor {

  private final Server server;
  private final String uuid;
  private final String metricName;
  private final String componentId;
  private final MonitorContext monitorContext;
  private final Iterable<DataSink> dataSinks;

  public PushMonitorImpl(Server server, String uuid, String metricName, String componentId,
      MonitorContext monitorContext, Iterable<DataSink> dataSinks) {
    this.server = server;
    this.uuid = uuid;
    this.metricName = metricName;
    this.componentId = componentId;
    this.monitorContext = monitorContext;
    this.dataSinks = dataSinks;
  }

  @Override
  public int port() {
    return server.port();
  }

  @Override
  public void start() {
    this.server.registerMonitor(metricName, this);
  }

  @Override
  public void stop() {
    this.server.unregisterMonitor(metricName);
  }

  @Override
  public String uuid() {
    return uuid;
  }

  @Override
  public String metricName() {
    return metricName;
  }

  @Override
  public String componentId() {
    return componentId;
  }

  @Override
  public MonitorContext monitorContext() {
    return monitorContext;
  }

  @Override
  public Iterable<DataSink> dataSinks() {
    return dataSinks;
  }
}
