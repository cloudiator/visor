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

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.visor.exceptions.InvalidMonitorContextException;
import de.uniulm.omi.cloudiator.visor.exceptions.MonitorException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorCreationException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.execution.ScheduledExecutionService;
import de.uniulm.omi.cloudiator.visor.reporting.QueuedReporting;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import de.uniulm.omi.cloudiator.visor.server.ServerRegistry;

import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Created by daniel on 15.01.15.
 */
public class MonitorFactoryImpl implements MonitorFactory {

    private final ReportingInterface<Metric> metricReportingInterface;
    private final SensorFactory sensorFactory;
    private final MonitorContextFactory monitorContextFactory;
    private final ServerRegistry serverRegistry;
    private final ScheduledExecutionService executionService;

    @Inject public MonitorFactoryImpl(ServerRegistry serverRegistry,
        MonitorContextFactory monitorContextFactory, SensorFactory sensorFactory,
        @QueuedReporting ReportingInterface<Metric> metricReportingInterface,
        ScheduledExecutionService executionService) {
        this.serverRegistry = serverRegistry;
        this.monitorContextFactory = monitorContextFactory;
        this.sensorFactory = sensorFactory;
        this.metricReportingInterface = metricReportingInterface;
        this.executionService = executionService;
    }

    @Override public SensorMonitor create(String uuid, String metricName, String componentId,
        Map<String, String> monitorContext, String sensorClassName, Interval interval,
        SensorConfiguration sensorConfiguration) throws MonitorException {
        try {
            final MonitorContext context = monitorContextFactory.create(monitorContext);
            return new SensorMonitorImpl(uuid, metricName, componentId,
                sensorFactory.from(sensorClassName, sensorConfiguration, context), interval,
                context, metricReportingInterface, executionService);
        } catch (InvalidMonitorContextException | SensorInitializationException | SensorCreationException e) {
            throw new MonitorException("Unable to create monitor due to error", e);
        }
    }

    @Override public PushMonitor create(String uuid, String metricName, String componentId,
        Map<String, String> monitorContext, @Nullable Integer port) throws MonitorException {

        try {
            MonitorContext context = monitorContextFactory.create(monitorContext);
            return new PushMonitorImpl(serverRegistry.getServer(componentId, port), uuid, metricName,
                componentId, context);
        } catch (IOException e) {
            throw new MonitorException("Unable to create monitor due to error", e);
        }
    }
}
