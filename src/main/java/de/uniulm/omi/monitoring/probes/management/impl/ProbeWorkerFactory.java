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

package de.uniulm.omi.monitoring.probes.management.impl;

import com.google.inject.Inject;
import de.uniulm.omi.monitoring.metric.api.MetricFactoryInterface;
import de.uniulm.omi.monitoring.metric.impl.Metric;
import de.uniulm.omi.monitoring.reporting.modules.api.QueuedReporting;
import de.uniulm.omi.monitoring.probes.api.Probe;
import de.uniulm.omi.monitoring.probes.management.api.ProbeWorkerFactoryInterface;
import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;

/**
 * Created by daniel on 11.12.14.
 */
public class ProbeWorkerFactory implements ProbeWorkerFactoryInterface {

    private final ReportingInterface<Metric> metricReportingInterface;
    private final MetricFactoryInterface metricFactory;

    @Inject
    public ProbeWorkerFactory(@QueuedReporting ReportingInterface<Metric> metricReportingInterface, MetricFactoryInterface metricFactory) {
        this.metricReportingInterface = metricReportingInterface;
        this.metricFactory = metricFactory;
    }

    @Override
    public ProbeWorker create(Probe probe) {
        return new ProbeWorker(probe, this.metricReportingInterface, this.metricFactory);
    }
}
