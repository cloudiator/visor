package de.uniulm.omi.monitoring;

public class KairoReporting implements ReportingInterface {

	public void put(Metric metric) {
		System.out.println(String.format("put %s %s %s", metric.name,
				metric.value, metric.timestamp));
	}
}
