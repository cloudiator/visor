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

package de.uniulm.omi.cloudiator.visor.rest.converters;

import de.uniulm.omi.cloudiator.visor.monitoring.Monitor;
import de.uniulm.omi.cloudiator.visor.rest.entities.MonitorDto;
import de.uniulm.omi.cloudiator.visor.rest.entities.MonitorDtoBuilder;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Created by daniel on 27.10.15.
 */
public class MonitorConverter implements Function<Monitor, MonitorDto> {

    @Nullable @Override public MonitorDto apply(Monitor monitor) {
        return new MonitorDtoBuilder().interval(monitor.getInterval())
            .metricName(monitor.getMetricName())
            .monitorContext(monitor.getMonitorContext().getContext())
            .sensorClassName(monitor.getSensor().getClass().getCanonicalName())
            .build();
    }
}
