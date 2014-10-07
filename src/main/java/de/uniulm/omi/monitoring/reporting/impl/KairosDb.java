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

package de.uniulm.omi.monitoring.reporting.impl;

import de.uniulm.omi.monitoring.metric.impl.Metric;
import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.response.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;

public class KairosDb implements ReportingInterface<Metric> {

    protected String server;
    protected String port;

    private static final Logger logger = LogManager.getLogger(KairosDb.class);

    public KairosDb(String server, String port) {
        this.server = server;
        this.port = port;
    }

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
