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
import de.uniulm.omi.cloudiator.visor.server.Server;
import de.uniulm.omi.cloudiator.visor.server.ServerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

/**
 * Created by daniel on 11.12.14.
 */
@Singleton public class MonitoringServiceImpl implements MonitoringService {

    public static final int LOWER_PORT_BOUNDARY = 49152;
    public static final int UPPER_PORT_BOUNDARY = 65535;
    private final Map<String, Monitor> monitorRegistry;
    private final Map<String, Server> serverRegistry;
    private final ScheduledExecutionService scheduler;
    private final SensorFactory sensorFactory;
    private final MonitorFactory monitorFactory;
    private final ServerFactory serverFactory;
    private final MonitorContextFactory monitorContextFactory;

    @Inject
    public MonitoringServiceImpl(ScheduledExecutionService scheduler, SensorFactory sensorFactory,
        MonitorFactory monitorFactory, ServerFactory serverFactory,
        MonitorContextFactory monitorContextFactory) {
        this.scheduler = scheduler;
        this.sensorFactory = sensorFactory;
        this.monitorFactory = monitorFactory;
        this.serverFactory = serverFactory;
        this.monitorContextFactory = monitorContextFactory;
        monitorRegistry = new HashMap<>();
        serverRegistry = new HashMap<>();
    }

    @Override public void startMonitor(String uuid, String metricName, String sensorClassName,
        Interval interval, Map<String, String> monitorContext)
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

        final Sensor sensor = this.sensorFactory.from(sensorClassName);
        final Monitor monitor = this.monitorFactory.create(uuid, metricName, sensor, interval,
            this.monitorContextFactory.create(monitorContext));
        this.monitorRegistry.put(uuid, monitor);
        this.scheduler.schedule(monitor);
    }

    @Override public void stopMonitor(String uuid) {
        checkArgument(isMonitoring(uuid));
        this.scheduler.remove(this.monitorRegistry.get(uuid), false);
        this.monitorRegistry.remove(uuid);
    }

    @Override
    public void startServer(String uuid, Map<String, String> monitorContext, @Nullable Integer port)
        throws IOException {
        checkNotNull(uuid);
        checkArgument(!uuid.isEmpty());
        checkNotNull(monitorContext);
        Server server;
        if (port == null) {
            server = this.serverFactory.createServer(uuid, LOWER_PORT_BOUNDARY, UPPER_PORT_BOUNDARY,
                monitorContextFactory.create(monitorContext));
        } else {
            server = this.serverFactory
                .createServer(uuid, port, monitorContextFactory.create(monitorContext));
        }
        this.serverRegistry.put(uuid, server);
        this.scheduler.execute(server);
    }

    @Override public void stopServer(String uuid) {
        checkNotNull(uuid);
        checkArgument(!uuid.isEmpty());
        checkState(serverRegistry.containsKey(uuid));
        this.scheduler.remove(serverRegistry.get(uuid), true);

    }

    @Override public Collection<Monitor> getMonitors() {
        return this.monitorRegistry.values();
    }

    @Override public Collection<Server> getServers() {
        return this.serverRegistry.values();
    }

    @Override public Monitor getMonitor(String uuid) {
        return this.monitorRegistry.get(uuid);
    }

    @Override public Server getServer(String uuid) {
        return this.serverRegistry.get(uuid);
    }

    @Override public boolean isMonitoring(String uuid) {
        return this.monitorRegistry.containsKey(uuid);
    }
}
