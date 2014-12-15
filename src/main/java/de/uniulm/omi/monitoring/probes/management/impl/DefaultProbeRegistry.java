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

package de.uniulm.omi.monitoring.probes.management.impl;

import com.google.inject.Inject;
import de.uniulm.omi.monitoring.probes.Interval;
import de.uniulm.omi.monitoring.probes.impl.CpuUsageProbe;
import de.uniulm.omi.monitoring.probes.impl.MemoryUsageProbe;
import de.uniulm.omi.monitoring.probes.management.api.ProbeRegistryInterface;
import de.uniulm.omi.monitoring.probes.management.api.ProbeWorkerFactoryInterface;
import de.uniulm.omi.monitoring.execution.api.ScheduledExecutionServiceInterface;

import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 11.12.14.
 */
public class DefaultProbeRegistry extends ProbeRegistry {

    @Inject
    public DefaultProbeRegistry(ScheduledExecutionServiceInterface scheduler, ProbeWorkerFactoryInterface probeWorkerFactory) {
        super(scheduler, probeWorkerFactory);
        this.registerDefaultProbes(this);
    }

    public void registerDefaultProbes(ProbeRegistryInterface probeRegistry) {
        probeRegistry.registerProbe(new CpuUsageProbe(), new Interval(1, TimeUnit.SECONDS));
        probeRegistry.registerProbe(new MemoryUsageProbe(), new Interval(1, TimeUnit.SECONDS));
    }

}
