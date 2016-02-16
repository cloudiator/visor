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
import de.uniulm.omi.cloudiator.visor.exceptions.MonitorException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by daniel on 11.12.14.
 */
@Singleton public class MonitoringServiceImpl implements MonitoringService {

    private final Map<String, Monitor> monitorRegistry;
    private final MonitorFactory monitorFactory;

    @Inject public MonitoringServiceImpl(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
        monitorRegistry = new HashMap<>();
    }

    @Override public SensorMonitor startMonitor(String uuid, String componentId, String metricName,
        String sensorClassName, Interval interval, Map<String, String> monitorContext,
        SensorConfiguration sensorConfiguration) throws MonitorException {

        checkArgument(!monitorRegistry.containsKey(uuid),
            String.format("A monitor with the given uuid %s is already registered.", uuid));

        final SensorMonitor sensorMonitor = this.monitorFactory
            .create(uuid, metricName, componentId, monitorContext, sensorClassName, interval,
                sensorConfiguration);

        this.monitorRegistry.put(uuid, sensorMonitor);
        sensorMonitor.start();
        return sensorMonitor;
    }

    @Override public Monitor startMonitor(String uuid, String componentId, String metricName,
        Map<String, String> monitorContext) throws MonitorException {

        checkArgument(!monitorRegistry.containsKey(uuid),
            String.format("A monitor with the given uuid %s is already registered.", uuid));

        final PushMonitor pushMonitor =
            monitorFactory.create(uuid, metricName, componentId, monitorContext);
        monitorRegistry.put(uuid, pushMonitor);

        this.monitorRegistry.put(uuid, pushMonitor);

        pushMonitor.start();

        return pushMonitor;
    }

    @Override public Collection<Monitor> getMonitors() {
        return this.monitorRegistry.values();
    }

    @Override public Optional<Monitor> getMonitor(String uuid) {
        return Optional.ofNullable(this.monitorRegistry.get(uuid));
    }

    @Override public boolean isMonitoring(String uuid) {
        return this.monitorRegistry.containsKey(uuid);
    }

    @Override public void stopMonitor(String uuid) {
        checkArgument(monitorRegistry.containsKey(uuid),
            String.format("No monitor with id %s was registered", uuid));
        monitorRegistry.get(uuid).stop();
        monitorRegistry.remove(uuid);
    }
}
