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

import de.uniulm.omi.monitoring.metric.Metric;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MetricQueue implements MetricReportingInterface {

	private final BlockingQueue<Metric> metricQueue;
    private final ExecutorService service;
    private final MetricReportingInterface metricReportingInterface;

    public MetricQueue(int numWorkers, MetricReportingInterface metricReportingInterface) {
        //set the kairo interface
        this.metricReportingInterface = metricReportingInterface;
        //initialize metric queue
        this.metricQueue = new LinkedBlockingQueue<Metric>();
        //initialize thread pool
        this.service = Executors.newFixedThreadPool(numWorkers);
        // create workers
        for(int i = 0; i<numWorkers; i++) {
            service.submit(new MetricReportingWorker(this.metricQueue, this.metricReportingInterface));
        }

    }

	public void report(Metric metric) {
		try {
			this.metricQueue.put(metric);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
