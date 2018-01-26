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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daniel on 25.03.15.
 */
public class ExtendedScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ExtendedScheduledThreadPoolExecutor.class);

  public ExtendedScheduledThreadPoolExecutor(int corePoolSize) {
    super(corePoolSize);
  }

  public ExtendedScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
    super(corePoolSize, threadFactory);
  }

  public ExtendedScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
    super(corePoolSize, handler);
  }

  public ExtendedScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory,
      RejectedExecutionHandler handler) {
    super(corePoolSize, threadFactory, handler);
  }

  public static ScheduledExecutorService create(int nThreads) {
    return new ExtendedScheduledThreadPoolExecutor(nThreads);
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
    if (t == null && r instanceof Future<?>) {
      try {
        if (((Future) r).isDone() && !((Future) r).isCancelled()) {
          ((Future) r).get();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        t = e.getCause();
      }
    }
    if (t != null) {
      LOGGER.error("Uncaught exception occurred during the execution of task.", t);
    }
  }
}
