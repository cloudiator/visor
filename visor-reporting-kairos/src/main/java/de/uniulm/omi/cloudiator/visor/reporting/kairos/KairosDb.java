/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.reporting.kairos;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.visor.exceptions.ConfigurationException;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.response.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A concrete implementation of the reporting interface, reporting
 * metrics to the kairos database.
 */
public class KairosDb implements ReportingInterface<Metric> {

    /**
     * The server of the kairos db.
     */
    protected final String server;
    /**
     * The port of the kairos db.
     */
    protected final int port;

    /**
     * A logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(KairosDb.class);

    @Inject public KairosDb(@Named("kairosServer") String server, @Named("kairosPort") int port) {
        checkNotNull(server);
        checkArgument(!server.isEmpty(), "Server must not be empty.");
        checkArgument(port > 0, "Port must be >0");
        this.server = server;
        this.port = port;
    }

    /**
     * Sends a metric to the kairos server.
     *
     * @param metricBuilder the metricbuilder containing the metrics.
     * @throws de.uniulm.omi.cloudiator.visor.reporting.ReportingException If the kairos server could not be reached.
     */
    protected void sendMetric(MetricBuilder metricBuilder) throws ReportingException {
        try {
            HttpClient client = new HttpClient("http://" + this.server + ":" + this.port);
            Response response = client.pushMetrics(metricBuilder);

            //check if response is ok
            if (response.getStatusCode() / 100 != 2) {
                LOGGER.error("Kairos DB reported error. Status code: " + response.getStatusCode());
                LOGGER.error("Error message: " + response.getErrors());
                throw new ReportingException();
            } else {
                LOGGER.debug("Kairos DB returned OK. Status code: " + response.getStatusCode());
            }
            client.shutdown();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new ConfigurationException(e);
        } catch (IOException e) {
            LOGGER.error("Could not request KairosDB.", e);
            throw new ReportingException(e);
        }
    }

    /**
     * Report method.
     * Converts the given metric to a metric kairos understands and sends them.
     *
     * @param metric the metric to report.
     * @throws ReportingException of the metric could not be converted. or sent to kairos.
     */
    @Override public void report(Metric metric) throws ReportingException {

        KairosMetricConverter kairosMetricConverter = new KairosMetricConverter();
        try {
            kairosMetricConverter.add(metric);
        } catch (KairosMetricConversionException e) {
            throw new ReportingException(e);
        }
        this.sendMetric(kairosMetricConverter.convert());
    }

    /**
     * Report method for multiple metrics.
     * Converts the metrics to metrics kairos can understand and sends them.
     *
     * @param metrics a collection of metrics.
     * @throws ReportingException if the metric could not be converted or sent to kairos.
     */
    @Override public void report(Collection<Metric> metrics) throws ReportingException {
        KairosMetricConverter kairosMetricConverter = new KairosMetricConverter();
        for (Metric metric : metrics) {
            try {
                kairosMetricConverter.add(metric);
            } catch (KairosMetricConversionException e) {
                throw new ReportingException(e);
            }
        }
        this.sendMetric(kairosMetricConverter.convert());
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this).add("server", server).add("port", port).toString();
    }
}
