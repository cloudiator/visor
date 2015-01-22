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

import de.uniulm.omi.executionware.agent.monitoring.metric.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.metric.api.Metric;
import de.uniulm.omi.executionware.agent.monitoring.monitors.api.Monitor;
import de.uniulm.omi.executionware.agent.reporting.api.ReportingInterface;
import de.uniulm.omi.executionware.agent.reporting.impl.ReportingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class
        MonitorWorker implements Runnable {

    private final Monitor monitor;
    private final ReportingInterface<Metric> metricReporting;

    private static final Logger logger = LogManager.getLogger(MonitorWorker.class);

    public MonitorWorker(Monitor monitor, ReportingInterface<Metric> metricReporting) {
        this.monitor = monitor;
        this.metricReporting = metricReporting;
    }

    @Override
    public void run() {
        try {
            this.metricReporting.report(monitor.getMetric());
        } catch (MeasurementNotAvailableException e) {
            logger.error(String.format("Could not retrieve metric"));
        } catch (ReportingException e) {
            logger.error("Could not report metric", e);
        } catch (Throwable t) {
            logger.fatal(t);
        }
    }
}
