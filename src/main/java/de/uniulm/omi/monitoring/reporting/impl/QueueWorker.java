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

import java.util.concurrent.BlockingQueue;

public abstract class QueueWorker<T> implements Runnable {

	private BlockingQueue<T> queue;

	public QueueWorker(BlockingQueue<T> queue) {
		this.queue = queue;
	}

	public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                T item = this.queue.take();
                this.consume(item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
	}

	abstract protected void consume(T item);

}
