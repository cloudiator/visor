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

package de.uniulm.omi.executionware.agent.reporting.impl.kairos;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.executionware.agent.monitoring.metric.api.Metric;
import de.uniulm.omi.executionware.agent.reporting.api.ReportingInterface;
import de.uniulm.omi.executionware.agent.reporting.impl.MetricReportingException;
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
    private static final Logger logger = LogManager.getLogger(KairosDb.class);

    @Inject
    public KairosDb(@Named("kairosServer") String server, @Named("kairosPort") int port) {
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
     * @throws de.uniulm.omi.executionware.agent.reporting.impl.MetricReportingException If the kairos server could not be reached.
     */
    protected void sendMetric(MetricBuilder metricBuilder) throws MetricReportingException {
        try {
            HttpClient client = new HttpClient("http://" + this.server + ":" + this.port);
            Response response = client.pushMetrics(metricBuilder);

            //check if response is ok
            if (response.getStatusCode() / 100 != 2) {
                logger.error("Kairos DB reported error. Status code: " + response.getStatusCode());
                logger.error("Error message: " + response.getErrors());
                throw new MetricReportingException();
            } else {
                logger.debug("Kairos DB returned OK. Status code: " + response.getStatusCode());
            }
            client.shutdown();
        } catch (MalformedURLException | URISyntaxException e) {
            logger.fatal("KairosDB URL is invalid.", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error("Could not request KairosDB.", e);
            throw new MetricReportingException(e);
        } catch (RuntimeException e) {
            logger.fatal(e);
        }
    }

    /**
     * Report method.
     * Converts the given metric to a metric kairos understands and sends them.
     *
     * @param metric the metric to report.
     * @throws MetricReportingException of the metric could not be converted. or sent to kairos.
     */
    @Override
    public void report(Metric metric) throws MetricReportingException {
        logger.debug(String.format("Reporting new metric: %s", metric));
        MetricConverter metricConverter = new MetricConverter();
        try {
            metricConverter.add(metric);
        } catch (MetricConversionException e) {
            throw new MetricReportingException(e);
        }
        this.sendMetric(metricConverter.convert());
    }

    /**
     * Report method for multiple metrics.
     * Converts the metrics to metrics kairos can understand and sends them.
     *
     * @param metrics a collection of metrics.
     * @throws MetricReportingException if the metric could not be converted or sent to kairos.
     */
    @Override
    public void report(Collection<Metric> metrics) throws MetricReportingException {
        MetricConverter metricConverter = new MetricConverter();
        for (Metric metric : metrics) {
            logger.debug(String.format("Reporting new metric: %s", metric));
            try {
                metricConverter.add(metric);
            } catch (MetricConversionException e) {
                throw new MetricReportingException(e);
            }
        }
        this.sendMetric(metricConverter.convert());
    }

}
