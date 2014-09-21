package de.uniulm.omi.monitoring;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MetricQueue implements MetricReportingInterface {

	private final BlockingQueue<Metric> metricQueue;
    private final ExecutorService service;
    private final ReportingInterface kairoInterface;

    public MetricQueue(int numWorkers, ReportingInterface kairoInterface) {
        //set the kairo interface
        this.kairoInterface = kairoInterface;
        //initialize metric queue
        this.metricQueue = new LinkedBlockingQueue<Metric>();
        //initialize thread pool
        this.service = Executors.newFixedThreadPool(numWorkers);
        // create workers
        for(int i = 0; i<numWorkers; i++) {
            service.submit(new MetricReportingWorker(this.metricQueue, this.kairoInterface));
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
