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

package de.uniulm.omi.monitoring.reporting.impl;

import de.uniulm.omi.monitoring.metric.impl.Metric;
import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;

import java.util.Collection;
import java.util.concurrent.*;

public class MetricQueue implements ReportingInterface<Metric> {

    private final BlockingQueue<Metric> metricQueue;
    private final ExecutorService executorService;
    private final ReportingInterface<Metric> reportingInterface;

    public MetricQueue(int numWorkers, ReportingInterface<Metric> reportingInterface) {
        //set the consumer
        this.reportingInterface = reportingInterface;
        //initialize metric queue
        this.metricQueue = new LinkedBlockingQueue<>();
        //initialize thread pool
        this.executorService = Executors.newFixedThreadPool(numWorkers);
        // create workers
        for (int i = 0; i < numWorkers; i++) {
            executorService.submit(new QueueWorker<>(this.metricQueue, this.reportingInterface));
        }
    }

    public void report(Metric metric) {
        try {
            this.metricQueue.put(metric);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void report(Collection<Metric> metrics) throws MetricReportingException {
        for (Metric metric : metrics) {
            this.report(metric);
        }
    }
}
