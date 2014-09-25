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

package de.uniulm.omi.monitoring.scheduler;

import de.uniulm.omi.monitoring.metric.MetricBuilder;
import de.uniulm.omi.monitoring.probes.api.Probe;
import de.uniulm.omi.monitoring.probes.impl.MetricNotAvailableException;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by daniel on 22.09.14.
 */
public class ProbeWorker implements Runnable {

    protected Probe probe;
    protected MetricReportingInterface metricReportingInterface;

    private static final Logger logger = LogManager.getLogger(ProbeWorker.class);

    public ProbeWorker(Probe probe, MetricReportingInterface metricReportingInterface) {
        this.probe = probe;
        this.metricReportingInterface = metricReportingInterface;
    }

    @Override
    public void run() {
        try {
            this.metricReportingInterface.report(MetricBuilder.getInstance().newMetric(probe.getMetricName(), probe.getMetricValue()));
        } catch (MetricNotAvailableException e) {
            logger.error(String.format("Could not retrieve metric %s", probe.getMetricName()));
        }
    }
}
