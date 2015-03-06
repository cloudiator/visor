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

package de.uniulm.omi.executionware.agent.monitoring.impl;

import com.google.common.base.Optional;
import de.uniulm.omi.executionware.agent.monitoring.api.*;
import de.uniulm.omi.executionware.agent.reporting.api.ReportingInterface;
import de.uniulm.omi.executionware.agent.reporting.impl.ReportingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by daniel on 18.12.14.
 */
public class MonitorImpl implements Monitor {

    private static final Logger logger = LogManager.getLogger(Monitor.class);
    private final String metricName;
    private final Sensor sensor;
    private final MonitorContext monitorContext;
    private final MonitorWorker monitorWorker;
    private final Interval interval;

    public MonitorImpl(String metricName, Sensor sensor, Interval interval, MonitorContext monitorContext, ReportingInterface<Metric> metricReportingInterface) throws InvalidMonitorContextException {
        this.metricName = metricName;
        this.sensor = sensor;
        this.monitorContext = monitorContext;
        this.sensor.setMonitorContext(monitorContext);
        this.monitorWorker = new MonitorWorker(this, metricReportingInterface);
        this.interval = interval;
    }

    @Override
    public String getMetricName() {
        return metricName;
    }

    @Override
    public Sensor getSensor() {
        return sensor;
    }

    @Override
    public MonitorContext getMonitorContext() {
        return monitorContext;
    }

    @Override
    public Interval getInterval() {
        return this.interval;
    }

    @Override
    public Runnable getRunnable() {
        return this.monitorWorker;
    }

    @Override
    public String toString() {
        return "Monitor{" +
                "metricName='" + metricName + '\'' +
                ", sensor=" + sensor +
                ", monitorContext=" + monitorContext +
                ", interval=" + interval +
                '}';
    }

    private class MonitorWorker implements Runnable {

        private final Monitor monitor;
        private final ReportingInterface<Metric> metricReportingInterface;

        public MonitorWorker(Monitor monitor, ReportingInterface<Metric> metricReportingInterface) {
            this.monitor = monitor;
            this.metricReportingInterface = metricReportingInterface;
        }

        @Override
        public void run() {
            try {
                logger.debug("Measuring Monitor " + this.monitor);
                this.metricReportingInterface.report(MetricFactory.from(monitor.getMetricName(), monitor.getSensor().getMeasurement(), monitor.getMonitorContext()));
            } catch (MeasurementNotAvailableException e) {
                logger.error(String.format("Could not retrieve metric"));
            } catch (ReportingException e) {
                logger.error("Could not report metric", e);
            } catch (Throwable t) {
                logger.fatal(t);
            }
        }
    }

}
