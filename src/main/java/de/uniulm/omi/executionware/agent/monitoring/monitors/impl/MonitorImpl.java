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

package de.uniulm.omi.executionware.agent.monitoring.monitors.impl;

import de.uniulm.omi.executionware.agent.monitoring.metric.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.metric.api.MetricFactory;
import de.uniulm.omi.executionware.agent.monitoring.metric.impl.Metric;
import de.uniulm.omi.executionware.agent.monitoring.monitors.api.Monitor;
import de.uniulm.omi.executionware.agent.monitoring.probes.api.Probe;

/**
 * Created by daniel on 18.12.14.
 */
public class MonitorImpl implements Monitor {

    private final String metricName;
    private final Probe probe;
    private final MetricFactory metricFactory;

    public MonitorImpl(String metricName, Probe probe, MetricFactory metricFactory) {
        this.metricName = metricName;
        this.probe = probe;
        this.metricFactory = metricFactory;
    }

    @Override
    public Metric getMetric() throws MeasurementNotAvailableException {
        return this.metricFactory.from(this.metricName, this.probe.getMeasurement());
    }
}
