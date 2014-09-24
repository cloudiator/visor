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

import de.uniulm.omi.monitoring.probes.api.Probe;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by daniel on 22.09.14.
 */
public class Scheduler {

    private final ScheduledExecutorService scheduledExecutorService;
    protected MetricReportingInterface metricReportingInterface;

    public Scheduler(int numOfWorkers, MetricReportingInterface metricReportingInterface) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(5);
        this.metricReportingInterface = metricReportingInterface;
    }

    public void registerProbe(Probe probe) {
        this.scheduledExecutorService.scheduleAtFixedRate(new ProbeWorker(probe, metricReportingInterface), 0, probe.getInterval().getPeriod(), probe.getInterval().getTimeUnit());
    }

    public void unregisterProbe(Probe probe) {

    }

}
