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

package de.uniulm.omi.monitoring.reporting.impl.queue;

import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;
import de.uniulm.omi.monitoring.reporting.impl.ReportingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Generic implementation of a queue worker.
 *
 * @param <T>
 */
public class QueueWorker<T> implements Runnable {

    private BlockingQueue<T> queue;
    private ReportingInterface<T> reportingInterface;
    private static final Logger logger = LogManager.getLogger(QueueWorker.class);

    public QueueWorker(BlockingQueue<T> queue, ReportingInterface<T> reportingInterface) {
        this.queue = queue;
        this.reportingInterface = reportingInterface;
    }

    public void run() {
        List<T> tList = new ArrayList<>();
        this.queue.drainTo(tList);
        try {
            logger.info("Reporting "+tList.size()+" items.");
            this.reportingInterface.report(tList);
        } catch (ReportingException e) {
            logger.error("Could not report metrics, throwing them away.", e);
        }
    }

}
