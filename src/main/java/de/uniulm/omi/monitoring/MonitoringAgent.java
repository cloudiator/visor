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

package de.uniulm.omi.monitoring;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uniulm.omi.monitoring.config.impl.BaseConfigurationModule;
import de.uniulm.omi.monitoring.execution.impl.ShutdownHook;
import de.uniulm.omi.monitoring.reporting.modules.impl.CommandLineReportingModule;
import de.uniulm.omi.monitoring.probes.management.impl.DefaultProbeRegistry;
import de.uniulm.omi.monitoring.server.config.ServerModule;
import de.uniulm.omi.monitoring.server.impl.SocketServer;
import org.apache.commons.cli.ParseException;


public class MonitoringAgent {

    public static void main(final String[] args) throws ParseException {

        final Injector injector = Guice.createInjector(new CommandLineReportingModule(), new ServerModule(), new BaseConfigurationModule(args));

        Runtime.getRuntime().addShutdownHook(injector.getInstance(ShutdownHook.class));
        injector.getInstance(DefaultProbeRegistry.class);
        injector.getInstance(SocketServer.class);
    }

}
