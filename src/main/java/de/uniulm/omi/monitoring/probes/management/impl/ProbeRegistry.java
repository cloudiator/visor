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

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import de.uniulm.omi.monitoring.probes.Interval;
import de.uniulm.omi.monitoring.probes.api.Probe;
import de.uniulm.omi.monitoring.probes.management.api.ProbeRegistryInterface;
import de.uniulm.omi.monitoring.probes.management.api.ProbeWorkerFactoryInterface;
import de.uniulm.omi.monitoring.execution.api.ScheduledExecutionServiceInterface;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 11.12.14.
 */
public class ProbeRegistry implements ProbeRegistryInterface {


    private final ScheduledExecutionServiceInterface scheduler;
    private final ProbeWorkerFactoryInterface probeWorkerFactory;

    @Inject
    public ProbeRegistry(ScheduledExecutionServiceInterface scheduler, ProbeWorkerFactoryInterface probeWorkerFactory) {
        this.scheduler = scheduler;
        this.probeWorkerFactory = probeWorkerFactory;
    }

    @Override
    public void registerProbe(final Probe probe, final Interval interval) {
        checkNotNull(probe);
        checkNotNull(interval);

        this.scheduler.schedule(this.probeWorkerFactory.create(probe), interval);
    }

    @Override
    public void changeInterval(final Probe probe, final Interval interval) {
        checkNotNull(probe);
        checkNotNull(interval);
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterProbe(final Probe probe) {
        checkNotNull(probe);
        throw new UnsupportedOperationException();
    }
}
