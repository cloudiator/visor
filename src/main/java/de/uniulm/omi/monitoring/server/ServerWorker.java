package de.uniulm.omi.monitoring.server;

import de.uniulm.omi.monitoring.metric.MetricBuilder;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by daniel on 22.09.14.
 */
public class ServerWorker implements Runnable {

    private InputStream inputStream;
    private MetricReportingInterface metricReportingInterface;

    private static final Logger logger = LogManager.getLogger(ServerWorker.class);

    public ServerWorker(InputStream inputStream, MetricReportingInterface metricReportingInterface) {
        this.inputStream = inputStream;
        this.metricReportingInterface = metricReportingInterface;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Scanner in = new Scanner(this.inputStream);
            while (in.hasNextLine()) {
                try {
                    this.metricReportingInterface.report(MetricBuilder.getInstance().newMetric(in.nextLine()));
                } catch (IllegalRequestException e) {
                    logger.error("Illegal request",e);
                }
            }
        }
    }
}
