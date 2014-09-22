package de.uniulm.omi.monitoring.reporting.impl;

import de.uniulm.omi.monitoring.metric.Metric;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.response.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class KairosDb implements MetricReportingInterface {

    protected String server;
    protected String port;

    private static final Logger logger = LogManager.getLogger(KairosDb.class);

    public KairosDb(String server, String port) {
        this.server = server;
        this.port = port;
    }

    @Override
    public void report(Metric metric) {
        logger.debug(String.format("Reporting new metric. Name: %s, Value: %s, Time: %s, Application: %s, IP: %s", metric.getName(), metric.getValue(), metric.getTimestamp(), metric.getApplicationName(), metric.getIp()));
        MetricBuilder builder = MetricBuilder.getInstance();
        builder.addMetric(metric.getName()).addDataPoint(metric.getTimestamp(), metric.getValue()).addTag("server", metric.getIp()).addTag("application", metric.getApplicationName());

        try {
            HttpClient client = new HttpClient("http://" + this.server + ":" + this.port);
            Response response = client.pushMetrics(builder);
            System.out.println(response.getStatusCode());
            System.out.println(response.getErrors());
            client.shutdown();
        } catch (MalformedURLException e) {
            logger.error("KairosDB URL is invalid.", e);
        } catch (IOException e) {
            logger.error("Could not request KairosDB.", e);
        } catch (URISyntaxException e) {
            logger.error("KairosDB URL is invalid.", e);
        }
    }
}
