package de.uniulm.omi.monitoring.reporting.api;

import de.uniulm.omi.monitoring.metric.Metric;

public interface MetricReportingInterface {

	public void report(Metric metric);
	
}
