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
import de.uniulm.omi.executionware.agent.server.impl.SocketServer;
import de.uniulm.omi.executionware.agent.monitoring.management.impl.DefaultProbeRegistry;

import java.util.Set;

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
        injector.getInstance(DefaultProbeRegistry.class);
        injector.getInstance(SocketServer.class);
        Runtime.getRuntime().addShutdownHook(injector.getInstance(ShutdownHook.class));
    }
}
