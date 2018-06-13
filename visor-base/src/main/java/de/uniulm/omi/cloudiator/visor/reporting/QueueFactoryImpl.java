/*
 * Copyright (c) 2014-2018 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.reporting;

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.visor.execution.ScheduledExecutionService;

public class QueueFactoryImpl<T> implements QueueFactory<T> {

  private final QueueWorkerFactory<T> queueWorkerFactory;
  private final ScheduledExecutionService scheduledExecutionService;

  @Inject
  QueueFactoryImpl(
      QueueWorkerFactory<T> queueWorkerFactory, ScheduledExecutionService executionService) {
    this.queueWorkerFactory = queueWorkerFactory;
    this.scheduledExecutionService = executionService;
  }

  @Override
  public ReportingInterface<T> queueReportingInterface(ReportingInterface<T> reportingInterface) {
    return new QueuedReportingInterface<>(scheduledExecutionService, reportingInterface,
        queueWorkerFactory);
  }
}
