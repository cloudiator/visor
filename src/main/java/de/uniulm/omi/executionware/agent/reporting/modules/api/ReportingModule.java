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

package de.uniulm.omi.executionware.agent.reporting.modules.api;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import de.uniulm.omi.executionware.agent.reporting.api.ReportingInterface;
import de.uniulm.omi.executionware.agent.metric.impl.Metric;
import de.uniulm.omi.executionware.agent.reporting.impl.queue.MetricQueue;
import de.uniulm.omi.executionware.agent.reporting.impl.queue.MetricQueueWorkerFactory;
import de.uniulm.omi.executionware.agent.reporting.impl.queue.QueueWorkerFactoryInterface;

/**
 * Created by daniel on 10.12.14.
 */
public abstract class ReportingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<QueueWorkerFactoryInterface<Metric>>() {
        }).to(MetricQueueWorkerFactory.class);

        bind(new TypeLiteral<ReportingInterface<Metric>>() {
        }).to(this.getReportingInterface());

        bind(new TypeLiteral<ReportingInterface<Metric>>() {
        }).annotatedWith(QueuedReporting.class).to(MetricQueue.class);

        //bind(new TypeLiteral<ReportingInterface<Metric>>() {
        //}).annotatedWith(QueuedReporting.class).to(Queue.class);

    }

    protected abstract Class<? extends ReportingInterface<Metric>> getReportingInterface();

}
