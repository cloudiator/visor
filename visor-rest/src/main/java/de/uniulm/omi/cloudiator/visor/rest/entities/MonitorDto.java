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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.uniulm.omi.cloudiator.visor.monitoring.DefaultInterval;
import de.uniulm.omi.cloudiator.visor.monitoring.Interval;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

/**
 * Created by daniel on 26.10.15.
 */
public class MonitorDto {

    @NotNull private String metricName;
    @NotNull private String sensorClassName;
    private Map<String, String> monitorContext;
    @NotNull @JsonSerialize(as = DefaultInterval.class) @JsonDeserialize(as = DefaultInterval.class)
    private Interval interval;

    protected MonitorDto() {

    }

    public MonitorDto(String metricName, String sensorClassName, Map<String, String> monitorContext,
        Interval interval) {
        this.metricName = metricName;
        this.sensorClassName = sensorClassName;
        this.monitorContext = monitorContext;
        this.interval = interval;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getSensorClassName() {
        return sensorClassName;
    }

    public void setSensorClassName(String sensorClassName) {
        this.sensorClassName = sensorClassName;
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

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }
}
