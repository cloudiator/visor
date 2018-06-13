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

package de.uniulm.omi.cloudiator.visor.reporting;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.visor.exceptions.ConfigurationException;
import de.uniulm.omi.cloudiator.visor.monitoring.Intervals;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 12.12.14.
 */
public class QueueWorkerFactory<T> implements QueueWorkerFactoryInterface<T> {

  private final int reportingInterval;

  @Inject
  public QueueWorkerFactory(@Named("reportingInterval") int reportingInterval) {
    if (reportingInterval < 0) {
      throw new ConfigurationException("Reporting interval is less than zero");
    }

    this.reportingInterval = reportingInterval;
  }

  @Override
  public Runnable create(ReportingInterface<T> reportingInterface, BlockingQueue<T> queue) {

    if (reportingInterval == 0) {
      return new BlockingQueueWorker<>(queue, reportingInterface);
    } else {
      return new SchedulableQueueWorker<T>(queue, reportingInterface,
          Intervals.of(reportingInterval, TimeUnit.SECONDS));
    }
  }
}
