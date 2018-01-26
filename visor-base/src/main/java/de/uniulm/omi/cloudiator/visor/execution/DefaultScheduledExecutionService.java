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

package de.uniulm.omi.cloudiator.visor.execution;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daniel on 11.12.14.
 */
@Singleton
public class DefaultScheduledExecutionService implements ScheduledExecutionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledExecutionService.class);

  /**
   * The executor service used for scheduling the probe runs.
   */
  private final ScheduledExecutorService scheduledExecutorService;

  private final Map<Schedulable, Future> registeredSchedulables;

  @Inject
  public DefaultScheduledExecutionService(@Named("executionThreads") int executionThreads) {
    checkArgument(executionThreads >= 1, "Execution thread must be >= 1");
    LOGGER.debug(String.format("Starting execution service with %s threads", executionThreads));
    scheduledExecutorService = ExtendedScheduledThreadPoolExecutor.create(executionThreads);
    registeredSchedulables = new HashMap<>();
  }

  @Override
  public void schedule(Schedulable schedulable) {
    checkNotNull(schedulable);
    LOGGER.debug(
        "Scheduling " + schedulable.getClass().getName() + " with interval of " + schedulable
            .getInterval());
    final ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService
        .scheduleAtFixedRate(schedulable, 0, schedulable.getInterval().getPeriod(),
            schedulable.getInterval().getTimeUnit());
    this.registeredSchedulables.put(schedulable, scheduledFuture);
  }

  @Override
  public void reschedule(Schedulable schedulable) {
    checkNotNull(schedulable);
    this.remove(schedulable, true);
    this.schedule(schedulable);
  }

  @Override
  public void execute(Runnable runnable) {
    checkNotNull(runnable);
    this.scheduledExecutorService.execute(runnable);
  }

  @Override
  public void remove(Schedulable schedulable, boolean force) {
    checkNotNull(schedulable);
    checkState(this.registeredSchedulables.containsKey(schedulable),
        schedulable + " was never registered.");
    this.registeredSchedulables.get(schedulable).cancel(force);
    this.registeredSchedulables.remove(schedulable);
  }

  @Override
  public void shutdown(final int seconds) {

    LOGGER.debug(String.format("Shutting down execution service in %d seconds", seconds));

    try {
      // Wait a while for existing tasks to terminate
      if (!this.scheduledExecutorService.awaitTermination(seconds, TimeUnit.SECONDS)) {
        this.scheduledExecutorService.shutdownNow();
        if (!this.scheduledExecutorService.awaitTermination(seconds, TimeUnit.SECONDS)) {
          LOGGER.error("Execution pool did not terminate.");
        }
      }
    } catch (InterruptedException ie) {
      this.scheduledExecutorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void kill() {
    this.scheduledExecutorService.shutdownNow();
  }
}
