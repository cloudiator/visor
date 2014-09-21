package de.uniulm.omi.monitoring;

import java.util.concurrent.BlockingQueue;

public class MetricReportingWorker extends QueueWorker<Metric> {

	public MetricReportingWorker(BlockingQueue<Metric> queue,
			ReportingInterface kairoInterface) {
		super(queue);
		this.kairoInterface = kairoInterface;
	}

	private ReportingInterface kairoInterface;

    @Override
	protected void consume(Metric item) {
		this.kairoInterface.put(item);
	}

}
