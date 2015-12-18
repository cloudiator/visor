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
import com.google.inject.Singleton;
import de.uniulm.omi.cloudiator.visor.execution.ScheduledExecutionService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 11.12.14.
 */
@Singleton public class MonitoringServiceImpl implements MonitoringService {

    private final Map<String, Monitor> monitorRegistry;
    private final ScheduledExecutionService scheduler;
    private final SensorFactory sensorFactory;
    private final MonitorFactory monitorFactory;

    @Inject
    public MonitoringServiceImpl(ScheduledExecutionService scheduler, SensorFactory sensorFactory,
        MonitorFactory monitorFactory) {
        this.scheduler = scheduler;
        this.sensorFactory = sensorFactory;
        this.monitorFactory = monitorFactory;
        monitorRegistry = new HashMap<>();
    }

    @Override public void startMonitoring(String uuid, String metricName, String sensorClassName,
        String sensorSourceUri, Interval interval, Map<String, String> monitorContext)
        throws SensorNotFoundException, SensorInitializationException,
        InvalidMonitorContextException {

        checkNotNull(uuid);
        checkArgument(!uuid.isEmpty());

        checkNotNull(uuid);
        checkArgument(!uuid.isEmpty());

        checkNotNull(metricName);
        checkArgument(!metricName.isEmpty());

        checkNotNull(sensorClassName);
        checkArgument(!sensorClassName.isEmpty());

        checkNotNull(interval);

        checkNotNull(monitorContext);

        final Sensor sensor = (sensorSourceUri == null ? 
        							this.sensorFactory.from(sensorClassName) :
        							this.sensorFactory.fromUriSource(sensorSourceUri, sensorClassName));
        final Monitor monitor =
            this.monitorFactory.create(uuid, sensorSourceUri, metricName, sensor, interval, monitorContext);
        this.monitorRegistry.put(uuid, monitor);
        this.scheduler.schedule(monitor);
    }

    @Override public void stopMonitoring(String uuid) {
        checkArgument(isMonitoring(uuid));
        this.scheduler.remove(this.monitorRegistry.get(uuid));
        this.monitorRegistry.remove(uuid);
    }

    @Override public Collection<Monitor> getMonitors() {
        return this.monitorRegistry.values();
    }

    @Override public Monitor getMonitor(String uuid) {
        return this.monitorRegistry.get(uuid);
    }

    @Override public boolean isMonitoring(String uuid) {
        return this.monitorRegistry.containsKey(uuid);
    }
}
