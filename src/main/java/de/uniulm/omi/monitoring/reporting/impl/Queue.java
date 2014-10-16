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

package de.uniulm.omi.monitoring.reporting.impl;

import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A generic implementation of the reporting interface using a
 * queue as "buffer" for the reporting.
 * All items store in the queue will be later reported to the concrete reporting interface.
 *
 * @param <T> the class of the generic item.
 */
public class Queue<T> implements ReportingInterface<T> {

    /**
     * The queue storing the items.
     */
    private final BlockingQueue<T> queue;

    /**
     * Constructor for the queue.
     *
     * @param numWorkers         the number of worker threads reporting to the concrete interface.
     * @param reportingInterface the concrete reporting interface.
     */
    public Queue(int numWorkers, ReportingInterface<T> reportingInterface) {
        //initialize metric queue
        this.queue = new LinkedBlockingQueue<T>();
        //initialize thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(numWorkers);
        // create workers
        for (int i = 0; i < numWorkers; i++) {
            executorService.submit(new QueueWorker<T>(this.queue, reportingInterface));
        }
    }

    /**
     * Used to report an item to this queue.
     *
     * @param item the item to report.
     * @throws ReportingException if the queue is full.
     */
    public void report(T item) throws ReportingException {
        if (this.queue.remainingCapacity() == 0) {
            throw new ReportingException("Item could not be reported as queue is full.");
        }
        try {
            this.queue.put(item);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to report a collection of items to this queue.
     *
     * @param items a collection of generic items to report.
     * @throws ReportingException if the queue is full.
     */
    @Override
    public void report(Collection<T> items) throws ReportingException {
        for (T item : items) {
            this.report(item);
        }
    }
}
