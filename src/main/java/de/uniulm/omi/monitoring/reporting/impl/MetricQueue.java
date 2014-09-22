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
