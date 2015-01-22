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

package de.uniulm.omi.executionware.agent.monitoring.management.api;

import com.google.inject.ImplementedBy;
import de.uniulm.omi.executionware.agent.monitoring.management.impl.MonitorFactoryImpl;
import de.uniulm.omi.executionware.agent.monitoring.monitors.api.Monitor;
import de.uniulm.omi.executionware.agent.monitoring.monitors.impl.MonitorContext;
import de.uniulm.omi.executionware.agent.monitoring.sensors.api.Sensor;

/**
 * Created by daniel on 15.01.15.
 */
@ImplementedBy(MonitorFactoryImpl.class)
public interface MonitorFactory {
    public Monitor create(String metricName, Sensor sensor);

    Monitor create(String metricName, Sensor sensor, MonitorContext monitorContext);
}
