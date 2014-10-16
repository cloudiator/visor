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
import de.uniulm.omi.monitoring.probes.api.Probe;
import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * The scheduler for probes.
 * <p/>
 * Schedules the registered probes with their interval.
 */
public class ProbeScheduler {

    /**
     * A logger class.
     */
    private static final Logger logger = LogManager.getLogger(ProbeScheduler.class);

    /**
     * The executor service used for scheduling the probe runs.
     */
    private final ScheduledExecutorService scheduledExecutorService;
    /**
     * The reporting interface where the measured metrics are reported.
     *
     * @todo: dependency injection.
     */
    protected ReportingInterface<Metric> metricReportingInterface;

    private Map<Probe, ScheduledFuture> registeredProbes;

    /**
     * Constructor for the probe scheduler.
     *
     * @param numOfWorkers             the number of workers to use.
     * @param metricReportingInterface the reporting interface where the scheduler should report the metrics.
     */
    public ProbeScheduler(int numOfWorkers, ReportingInterface<Metric> metricReportingInterface) {
        logger.info(String.format("Initializing scheduler with %s workers.", numOfWorkers));
        this.scheduledExecutorService = Executors.newScheduledThreadPool(numOfWorkers);
        this.metricReportingInterface = metricReportingInterface;
        this.registeredProbes = new HashMap<Probe, ScheduledFuture>();
    }

    /**
     * Registers a probe with the scheduler. The scheduler will then
     * execute the registered probe with its interval.
     *
     * @param probe The probe which should be executed.
     */
    public void registerProbe(Probe probe) {
        logger.info(String.format("New probe for metric %s registered with interval %s - %s at scheduler. ", probe.getMetricName(), probe.getInterval().getPeriod(), probe.getInterval().getTimeUnit()));
        ScheduledFuture scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(new ProbeWorker(probe, metricReportingInterface), 0, probe.getInterval().getPeriod(), probe.getInterval().getTimeUnit());
        this.registeredProbes.put(probe, scheduledFuture);
    }

    /**
     * Unregister a probe from the scheduler. A currently running task is not interrupted,
     * but future tasks are canceled.
     *
     * @param probe the probe to unregister.
     */
    public void unregisterProbe(Probe probe) {
        ScheduledFuture scheduledFuture = this.registeredProbes.get(probe);
        if (scheduledFuture == null) {
            logger.error("Probe " + probe.getMetricName() + " could not be unregistered.");
            return;
        }
        scheduledFuture.cancel(false);
        this.registeredProbes.remove(probe);
        logger.info(String.format("Probe for metric %s was unregistered from scheduler.",probe.getMetricName()));
    }

}
