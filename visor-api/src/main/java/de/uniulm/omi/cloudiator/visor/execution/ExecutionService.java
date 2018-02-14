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
 * Created by daniel on 12.12.14.
 */
public interface ExecutionService {

  /**
   * Executes the runnable.
   *
   * @param runnable the runnable to be executed by the execution service.
   */
  void execute(Runnable runnable);

  /**
   * Gracefully shutdowns the execution service.
   * <p/>
   * Waits for the completion of the runnables until the timeout expired. Afterwards the execution
   * of the runnables is interrupted.
   *
   * @param seconds the timeout.
   */
  void shutdown(int seconds);

  /**
   * Instantly kills the execution service, interrupting the execution of all runnables.
   */
  void kill();
}
