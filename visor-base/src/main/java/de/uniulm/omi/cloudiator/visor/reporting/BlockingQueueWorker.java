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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockingQueueWorker<T> implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(BlockingQueueWorker.class);
  private final BlockingQueue<T> queue;
  private final ReportingInterface<T> reportingInterface;

  public BlockingQueueWorker(BlockingQueue<T> queue, ReportingInterface<T> reportingInterface) {
    this.queue = queue;
    this.reportingInterface = reportingInterface;
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        List<T> elements = new ArrayList<>();
        final T take = queue.take();
        elements.add(take);
        queue.drainTo(elements);
        reportingInterface.report(queue);
      } catch (InterruptedException e) {
        LOGGER.info(String.format("%s got interrupted. Stopping execution.", this));
        Thread.currentThread().interrupt();
      } catch (ReportingException e) {
        LOGGER.error("Could not report metrics, throwing them away.", e);
      } catch (Exception e) {
        LOGGER.error(String
            .format("Unexpected exception %s occurred in %s. Catching to allow further execution.",
                e.getMessage(), this), e);
      }
    }
  }
}
