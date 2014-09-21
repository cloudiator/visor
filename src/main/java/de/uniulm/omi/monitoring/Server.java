package de.uniulm.omi.monitoring;

/**
 * Created by daniel on 21.09.14.
 */
public class Server implements Runnable {

    protected final int port;
    protected final MetricReportingInterface metricReportingInterface;

    public Server(int port, MetricReportingInterface metricReportingInterface) {
        this.port = port;
        this.metricReportingInterface = metricReportingInterface;
    }

    @Override
    public void run() {
        while (true) {
            this.metricReportingInterface.report(new Metric("test", "1000"));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
