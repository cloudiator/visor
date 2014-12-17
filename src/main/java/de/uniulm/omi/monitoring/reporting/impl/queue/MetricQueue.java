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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniulm.omi.monitoring.execution.impl.ScheduledExecutionService;
import de.uniulm.omi.monitoring.metric.impl.Metric;

/**
 * Created by daniel on 15.12.14.
 */
@Singleton
public class MetricQueue extends Queue<Metric> {

    @Inject
    public MetricQueue(ScheduledExecutionService executionService, QueueWorkerFactoryInterface<Metric> queueWorkerFactory) {
        super(executionService, queueWorkerFactory);
    }
}
