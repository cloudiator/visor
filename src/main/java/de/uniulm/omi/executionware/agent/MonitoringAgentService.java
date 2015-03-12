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

package de.uniulm.omi.executionware.agent;/*
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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import de.uniulm.omi.executionware.agent.execution.impl.ShutdownHook;
import de.uniulm.omi.executionware.agent.monitoring.api.InvalidMonitorContextException;
import de.uniulm.omi.executionware.agent.monitoring.api.MonitoringService;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorNotFoundException;
import de.uniulm.omi.executionware.agent.monitoring.impl.Interval;
import de.uniulm.omi.executionware.agent.rest.RestServer;
import de.uniulm.omi.executionware.agent.server.impl.SocketServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 17.12.14.
 */
public class MonitoringAgentService {

    private final Set<Module> modules;

    public MonitoringAgentService(Set<Module> modules) {
        this.modules = modules;
    }

    public void start() {
        final Injector injector = Guice.createInjector(this.modules);
        try {
            injector.getInstance(MonitoringService.class).startMonitoring("cpu_usage", "de.uniulm.omi.executionware.agent.monitoring.sensors.CpuUsageSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
            injector.getInstance(MonitoringService.class).startMonitoring("memory_usage", "de.uniulm.omi.executionware.agent.monitoring.sensors.MemoryUsageSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
            injector.getInstance(MonitoringService.class).startMonitoring("sql_usage_nb_query", "de.uniulm.omi.executionware.agent.monitoring.sensors.MySQLSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
        } catch (SensorNotFoundException | InvalidMonitorContextException | SensorInitializationException e) {
            throw new RuntimeException(e);
        }
        injector.getInstance(SocketServer.class);
        injector.getInstance(RestServer.class);
        Runtime.getRuntime().addShutdownHook(injector.getInstance(ShutdownHook.class));
    }
}
