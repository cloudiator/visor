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

package de.uniulm.omi.executionware.agent.execution.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.uniulm.omi.executionware.agent.execution.api.ScheduledExecutionServiceInterface;
import de.uniulm.omi.executionware.agent.probes.Interval;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.*;

/**
 * Created by daniel on 11.12.14.
 */
@Singleton
public class ScheduledExecutionService implements ScheduledExecutionServiceInterface {

    private static final Logger logger = LogManager.getLogger(ScheduledExecutionService.class);

    /**
     * The executor service used for scheduling the probe runs.
     */
    private final ScheduledExecutorService scheduledExecutorService;

    private final Map<Runnable, ScheduledFuture> registeredWorkers;

    @Inject
    public ScheduledExecutionService(@Named("executionThreads") int executionThreads) {
        checkArgument(executionThreads >= 1, "Execution thread must be >= 1");
        logger.debug(String.format("Starting execution service with %s threads", executionThreads));
        scheduledExecutorService = Executors.newScheduledThreadPool(executionThreads);
        registeredWorkers = new HashMap<>();
    }

    @Override
    public void schedule(final Runnable runnable, final Interval interval) {
        checkNotNull(runnable);
        checkNotNull(interval);
        logger.debug("Scheduling " + runnable.getClass().getName() + " with interval of " + interval);
        final ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(runnable, 0, interval.getPeriod(), interval.getTimeUnit());
        this.registeredWorkers.put(runnable, scheduledFuture);
    }

    @Override
    public void remove(Runnable runnable) {
        checkNotNull(runnable);
        checkState(this.registeredWorkers.containsKey(runnable), "The runnable " + runnable + " was never registered with the scheduler.");
        this.registeredWorkers.get(runnable).cancel(false);
        this.registeredWorkers.remove(runnable);
    }

    @Override
    public void reschedule(Runnable runnable, Interval newInterval) {
        checkNotNull(runnable);
        checkNotNull(newInterval);

        this.remove(runnable);
        this.schedule(runnable, newInterval);
    }

    @Override
    public void execute(Runnable runnable) {
        checkNotNull(runnable);
        this.scheduledExecutorService.execute(runnable);
    }

    @Override
    public void shutdown(final int seconds) {

        logger.debug(String.format("Shutting down execution service in %d seconds", seconds));

        try {
            // Wait a while for existing tasks to terminate
            if (!this.scheduledExecutorService.awaitTermination(seconds, TimeUnit.SECONDS)) {
                this.scheduledExecutorService.shutdownNow();
                if (!this.scheduledExecutorService.awaitTermination(seconds, TimeUnit.SECONDS))
                    logger.error("Execution pool did not terminate.");
            }
        } catch (InterruptedException ie) {
            this.scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
