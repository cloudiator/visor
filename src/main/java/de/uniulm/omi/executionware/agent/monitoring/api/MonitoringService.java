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

package de.uniulm.omi.executionware.agent.monitoring.api;

import com.google.inject.ImplementedBy;
import de.uniulm.omi.executionware.agent.monitoring.impl.Interval;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitoringServiceImpl;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Created by daniel on 11.12.14.
 */
@ImplementedBy(MonitoringServiceImpl.class)
public interface MonitoringService {


    public void startMonitoring(String metricName, String sensorClassName, Interval interval, Map<String, String> monitorContext) throws SensorNotFoundException, SensorInitializationException, InvalidMonitorContextException;

    public void stopMonitoring(String metricName);

    public Collection<Monitor> getMonitors();

    public boolean isMonitoring(String metricName);
}
