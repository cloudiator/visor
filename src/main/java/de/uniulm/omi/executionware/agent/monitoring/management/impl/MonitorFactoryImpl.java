/*
 *
 *  * Copyright (c) 2014 University of Ulm
 *  *
 *  * See the NOTICE file distributed with this work for additional information
 *  * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package de.uniulm.omi.executionware.agent.monitoring.management.impl;

import com.google.inject.Inject;
import de.uniulm.omi.executionware.agent.monitoring.management.api.MonitorFactory;
import de.uniulm.omi.executionware.agent.monitoring.metric.api.MetricFactory;
import de.uniulm.omi.executionware.agent.monitoring.monitors.api.Monitor;
import de.uniulm.omi.executionware.agent.monitoring.monitors.impl.MonitorImpl;
import de.uniulm.omi.executionware.agent.monitoring.sensors.api.Sensor;

/**
 * Created by daniel on 15.01.15.
 */
public class MonitorFactoryImpl implements MonitorFactory {

    private final MetricFactory metricFactory;

    @Inject
    public MonitorFactoryImpl(MetricFactory metricFactory) {
        this.metricFactory = metricFactory;
    }

    @Override
    public Monitor create(String metricName, Sensor sensor) {
        return new MonitorImpl(metricName, sensor, this.metricFactory);
    }
}
