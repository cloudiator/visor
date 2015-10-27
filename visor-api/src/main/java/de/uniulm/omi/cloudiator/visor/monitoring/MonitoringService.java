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


import de.uniulm.omi.cloudiator.visor.server.Server;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by daniel on 11.12.14.
 */
public interface MonitoringService {

    void startMonitor(String uuid, String metricName, String sensorClassName, Interval interval,
        Map<String, String> monitorContext)
        throws SensorNotFoundException, SensorInitializationException,
        InvalidMonitorContextException;

    void stopMonitor(String uuid);

    void startServer(String uuid, Map<String, String> monitorContext, @Nullable Integer port)
        throws IOException;

    void stopServer(String uuid);

    Collection<Monitor> getMonitors();

    Collection<Server> getServers();

    Monitor getMonitor(String uuid);

    Server getServer(String uuid);

    boolean isMonitoring(String uuid);
}
