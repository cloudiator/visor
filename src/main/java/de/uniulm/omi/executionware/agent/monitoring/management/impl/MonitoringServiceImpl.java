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

package de.uniulm.omi.executionware.agent.monitoring.management.impl;

import com.google.inject.Inject;
import de.uniulm.omi.executionware.agent.execution.api.ScheduledExecutionServiceInterface;
import de.uniulm.omi.executionware.agent.monitoring.Interval;
import de.uniulm.omi.executionware.agent.monitoring.management.api.*;
import de.uniulm.omi.executionware.agent.monitoring.monitors.api.Monitor;
import de.uniulm.omi.executionware.agent.monitoring.sensors.api.Sensor;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by daniel on 11.12.14.
 */
public class MonitoringServiceImpl implements MonitoringService {


    private final Map<String, MonitorWorker> monitorRegistry;
    private final ScheduledExecutionServiceInterface scheduler;
    private final MonitorWorkerFactory monitorWorkerFactory;
    private final SensorService sensorService;
    private final MonitorFactory monitorFactory;

    @Inject
    public MonitoringServiceImpl(ScheduledExecutionServiceInterface scheduler, MonitorWorkerFactory monitorWorkerFactory, SensorService sensorService, MonitorFactory monitorFactory) {
        this.scheduler = scheduler;
        this.monitorWorkerFactory = monitorWorkerFactory;
        this.sensorService = sensorService;
        this.monitorFactory = monitorFactory;
        monitorRegistry = new HashMap<>();
    }

    @Override
    public void startMonitoring(String metricName, String probeClassName, Interval interval) throws SensorNotFoundException {
        final Sensor sensor = this.sensorService.findSensor(probeClassName);
        final Monitor monitor = this.monitorFactory.create(metricName, sensor);
        final MonitorWorker monitorWorker = this.monitorWorkerFactory.create(monitor);
        this.scheduler.schedule(monitorWorker, interval);
        this.monitorRegistry.put(metricName, monitorWorker);
    }

    @Override
    public void stopMonitoring(String metricName) {
        checkArgument(isMonitoring(metricName));
        this.scheduler.remove(this.monitorRegistry.get(metricName));
    }

    @Override
    public boolean isMonitoring(String metricName) {
        return this.monitorRegistry.containsKey(metricName);
    }
}
