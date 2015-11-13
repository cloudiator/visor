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

import de.uniulm.omi.cloudiator.visor.exceptions.MonitorException;
import de.uniulm.omi.cloudiator.visor.monitoring.Monitor;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitoringService;

import java.util.Map;

/**
 * Created by daniel on 11.11.15.
 */
public class PushMonitorDto extends MonitorDto {

    private Integer port;

    public PushMonitorDto(String metricName, String componentId, Map<String, String> monitorContext,
        Integer port) {
        super(metricName, componentId, monitorContext);
        this.port = port;
    }

    protected PushMonitorDto() {
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }


    @Override public Monitor start(String uuid, MonitoringService monitoringService)
        throws MonitorException {
        return monitoringService
            .startMonitor(uuid, getComponentId(), getMetricName(), getMonitorContext());
    }
}
