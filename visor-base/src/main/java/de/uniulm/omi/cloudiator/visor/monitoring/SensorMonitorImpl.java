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

package de.uniulm.omi.cloudiator.visor.monitoring;

import com.google.common.base.MoreObjects;
import de.uniulm.omi.cloudiator.visor.exceptions.InvalidMonitorContextException;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.execution.ScheduledExecutionService;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by daniel on 18.12.14.
 */
public class SensorMonitorImpl implements SensorMonitor {

    private static final Logger LOGGER = LogManager.getLogger(Monitor.class);
    private final String uuid;
    private final String metricName;
    private final String componentId;
    private final Sensor sensor;
    private final MonitorContext monitorContext;
    private final SensorMonitorWorker sensorMonitorWorker;
    private final Interval interval;
    private final ScheduledExecutionService executionService;

    public SensorMonitorImpl(String uuid, String metricName, String componentId, Sensor sensor,
        Interval interval, MonitorContext monitorContext,
        ReportingInterface<Metric> metricReportingInterface,
        ScheduledExecutionService executionService) throws InvalidMonitorContextException {
        this.uuid = uuid;
        this.metricName = metricName;
        this.componentId = componentId;
        this.sensor = sensor;
        this.monitorContext = monitorContext;
        this.sensorMonitorWorker = new SensorMonitorWorker(this, metricReportingInterface);
        this.interval = interval;
        this.executionService = executionService;
    }

    @Override public void start() {
        this.executionService.schedule(this);
    }

    @Override public void stop() {
        this.executionService.remove(this, false);
    }

    @Override public String uuid() {
        return this.uuid;
    }

    @Override public String metricName() {
        return metricName;
    }

    @Override public String componentId() {
        return componentId;
    }

    @Override public MonitorContext monitorContext() {
        return monitorContext;
    }

    @Override public Interval getInterval() {
        return this.interval;
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this).add("uuid", uuid).add("metricName", metricName)
            .add("sensor", sensor).add("context", monitorContext).add("interval", interval)
            .toString();

    }

    @Override public void run() {
        this.sensorMonitorWorker.run();
    }

    @Override public Class<? extends Sensor> sensorClass() {
        return sensor.getClass();
    }

    @Override public SensorConfiguration sensorConfiguration() {
        return sensor.sensorConfiguration();
    }

    private class SensorMonitorWorker implements Runnable {

        private final SensorMonitorImpl monitor;
        private final ReportingInterface<Metric> metricReportingInterface;

        public SensorMonitorWorker(SensorMonitorImpl monitor,
            ReportingInterface<Metric> metricReportingInterface) {
            this.monitor = monitor;
            this.metricReportingInterface = metricReportingInterface;
        }

        @Override public void run() {
            try {
                LOGGER.debug("Measuring Monitor " + this.monitor);
                this.metricReportingInterface.report(MetricFactory
                    .from(monitor.metricName(), monitor.sensor.getMeasurement(),
                        monitor.monitorContext().getContext()));
            } catch (MeasurementNotAvailableException e) {
                LOGGER.error("Could not retrieve metric", e);
            } catch (ReportingException e) {
                LOGGER.error("Could not report metric", e);
            }
        }
    }

}
