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

package de.uniulm.omi.executionware.agent.probes.management.impl;

import com.google.inject.Inject;
import de.uniulm.omi.executionware.agent.probes.management.api.ProbeWorkerFactoryInterface;
import de.uniulm.omi.executionware.agent.probes.strategies.impl.OSMxBeanMemoryStrategy;
import de.uniulm.omi.executionware.agent.execution.api.ScheduledExecutionServiceInterface;
import de.uniulm.omi.executionware.agent.probes.Interval;
import de.uniulm.omi.executionware.agent.probes.impl.CpuUsageProbe;
import de.uniulm.omi.executionware.agent.probes.impl.MemoryUsageProbe;
import de.uniulm.omi.executionware.agent.probes.management.api.ProbeRegistryInterface;
import de.uniulm.omi.executionware.agent.probes.strategies.impl.OSMxBeanCpuStrategy;

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
        probeRegistry.registerProbe(new CpuUsageProbe(new OSMxBeanCpuStrategy()), new Interval(1, TimeUnit.SECONDS));
        probeRegistry.registerProbe(new MemoryUsageProbe(new OSMxBeanMemoryStrategy()), new Interval(1, TimeUnit.SECONDS));
    }

}
