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


/**
 * Created by daniel on 11.12.14.
 */
public interface ScheduledExecutionService extends ExecutionService {

    /**
     * Schedules the schedulable within the execution service.
     *
     * @param schedulable the schedulable to schedule.
     */
    void schedule(Schedulable schedulable);

    /**
     * Reschedules the schedulable.
     * <p>
     * Can be used if the scheduling interval should be changed.
     *
     * @param schedulable the schedulable to be rescheduled.
     */
    void reschedule(Schedulable schedulable);

    /**
     * Stops the execution of the given runnable.
     *
     * @param schedulable the runnable to stop
     * @param force       if the execution of the runnable should be force quit (true), or allowed to finish execution (false)
     */
    void remove(Schedulable schedulable, boolean force);
}
