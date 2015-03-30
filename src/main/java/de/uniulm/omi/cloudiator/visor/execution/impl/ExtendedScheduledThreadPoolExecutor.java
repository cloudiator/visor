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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

/**
 * Created by daniel on 25.03.15.
 */
public class ExtendedScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public static ScheduledExecutorService create(int nThreads) {
        return new ExtendedScheduledThreadPoolExecutor(nThreads);
    }

    private static final Logger LOGGER =
        LogManager.getLogger(ExtendedScheduledThreadPoolExecutor.class);

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

    @Override protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                if (((Future) r).isDone()) {
                    ((Future) r).get();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                t = e.getCause();
            }
        }
        if (t != null) {
            LOGGER.fatal("Uncaught exception occurred during the execution of task.", t);
        }
    }
}
