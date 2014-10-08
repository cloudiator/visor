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

package de.uniulm.omi.monitoring.probes.impl.scheduler;

import de.uniulm.omi.monitoring.metric.impl.Metric;
import de.uniulm.omi.monitoring.metric.impl.MetricFactory;
import de.uniulm.omi.monitoring.probes.api.Probe;
import de.uniulm.omi.monitoring.probes.impl.MetricNotAvailableException;
import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;
import de.uniulm.omi.monitoring.reporting.impl.ReportingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The ProbeWorker class.
 * <p/>
 * The worker responsible for doing the probe measurements.
 * <p/>
 * It takes the measurements defined in the probe, and reports them
 * to the given metric reporting interface.
 */
public class ProbeWorker implements Runnable {

    /**
     * The probe this worker measures.
     */
    protected Probe probe;
    /**
     * The reporting interface this worker uses to report the metrics.
     *
     * @todo: dependency injection?
     */
    protected ReportingInterface<Metric> metricReportingInterface;

    /**
     * A logger.
     */
    private static final Logger logger = LogManager.getLogger(ProbeWorker.class);

    /**
     * Constructor for the probe worker.
     *
     * @param probe                    the probe it measures.
     * @param metricReportingInterface The reporting interface it uses to report the collected metrics.
     */
    public ProbeWorker(Probe probe, ReportingInterface<Metric> metricReportingInterface) {
        this.probe = probe;
        this.metricReportingInterface = metricReportingInterface;
    }

    /**
     * The run method.
     * Takes the probe measurement and reports it to the reporting interface.
     */
    @Override
    public void run() {
        logger.debug(String.format("Measuring probe %s at %s", this.probe.getMetricName(), System.currentTimeMillis()));
        try {
            this.metricReportingInterface.report(MetricFactory.getInstance().fromNameAndValue(this.probe.getMetricName(), this.probe.getMetricValue()));
        } catch (MetricNotAvailableException e) {
            logger.error(String.format("Could not retrieve metric %s", probe.getMetricName()));
        } catch (ReportingException e) {
            logger.error("Could not report metric", e);
        } catch (RuntimeException e) {
            logger.error(e);
        }
    }
}
