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

package de.uniulm.omi.cloudiator.visor.execution.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.visor.execution.api.Schedulable;
import de.uniulm.omi.cloudiator.visor.execution.api.ScheduledExecutionServiceInterface;
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

    private final Map<Schedulable, ScheduledFuture> registeredSchedulables;

    @Inject
    public ScheduledExecutionService(@Named("executionThreads") int executionThreads) {
        checkArgument(executionThreads >= 1, "Execution thread must be >= 1");
        logger.debug(String.format("Starting execution service with %s threads", executionThreads));
        scheduledExecutorService = Executors.newScheduledThreadPool(executionThreads);
        registeredSchedulables = new HashMap<>();
    }

    @Override
    public void schedule(Schedulable schedulable) {
        checkNotNull(schedulable);
        logger.debug("Scheduling " + schedulable.getClass().getName() + " with interval of " + schedulable.getInterval());
        final ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(
                schedulable.getRunnable(), 0, schedulable.getInterval().getPeriod(), schedulable.getInterval().getTimeUnit());
        this.registeredSchedulables.put(schedulable, scheduledFuture);
    }

    @Override
    public void remove(Schedulable schedulable) {
        checkNotNull(schedulable);
        checkState(this.registeredSchedulables.containsKey(schedulable), "The schedulable " + schedulable + " was never registered with the scheduler.");
        this.registeredSchedulables.get(schedulable).cancel(false);
        this.registeredSchedulables.remove(schedulable);
    }

    @Override
    public void reschedule(Schedulable schedulable) {
        checkNotNull(schedulable);
        this.remove(schedulable);
        this.schedule(schedulable);
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
