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
import de.uniulm.omi.executionware.agent.monitoring.metric.api.MetricFactoryInterface;
import de.uniulm.omi.executionware.agent.monitoring.metric.impl.ServerMetric;
import de.uniulm.omi.executionware.agent.monitoring.monitors.api.Monitor;
import de.uniulm.omi.executionware.agent.monitoring.probes.api.ServerProbe;

/**
 * Created by daniel on 18.12.14.
 */
public class ServerMonitor implements Monitor {

    private final String metricName;
    private final ServerProbe serverProbe;
    private final MetricFactoryInterface metricFactory;

    public ServerMonitor(String metricName, ServerProbe serverProbe, MetricFactoryInterface metricFactory) {
        this.metricName = metricName;
        this.serverProbe = serverProbe;
        this.metricFactory = metricFactory;
    }

    @Override
    public ServerMetric getMetric() throws MeasurementNotAvailableException {
        return this.metricFactory.from(this.metricName, this.serverProbe.getMeasurementValue());
    }
}
