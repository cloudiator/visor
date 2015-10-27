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

import de.uniulm.omi.cloudiator.visor.monitoring.Interval;

import java.util.Map;

public class MonitorDtoBuilder {
    private String metricName;
    private String sensorClassName;
    private Map<String, String> monitorContext;
    private Interval interval;

    public MonitorDtoBuilder metricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    public MonitorDtoBuilder sensorClassName(String sensorClassName) {
        this.sensorClassName = sensorClassName;
        return this;
    }

    public MonitorDtoBuilder monitorContext(Map<String, String> monitorContext) {
        this.monitorContext = monitorContext;
        return this;
    }

    public MonitorDtoBuilder interval(Interval interval) {
        this.interval = interval;
        return this;
    }

    public MonitorDto build() {
        return new MonitorDto(metricName, sensorClassName, monitorContext, interval);
    }
}
