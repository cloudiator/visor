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

import de.uniulm.omi.cloudiator.visor.monitoring.SensorMonitor;
import de.uniulm.omi.cloudiator.visor.rest.entities.SensorMonitorDto;
import de.uniulm.omi.cloudiator.visor.rest.entities.SensorMonitorDtoBuilder;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Created by daniel on 27.10.15.
 */
public class SensorMonitorConverter implements Function<SensorMonitor, SensorMonitorDto> {

    @Nullable @Override public SensorMonitorDto apply(SensorMonitor monitor) {
        return new SensorMonitorDtoBuilder().uuid(monitor.uuid()).componentId(monitor.componentId())
            .interval(monitor.getInterval()).metricName(monitor.metricName())
            .sensorConfiguration(monitor.sensorConfiguration().getConfiguration())
            .monitorContext(monitor.monitorContext().getContext())
            .sensorClassName(monitor.sensorClass().getName()).build();
    }
}
