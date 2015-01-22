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

package de.uniulm.omi.executionware.agent.reporting.impl.kairos;

import de.uniulm.omi.executionware.agent.monitoring.metric.api.Metric;
import org.kairosdb.client.builder.MetricBuilder;

/**
 * Created by daniel on 23.09.14.
 */
public class MetricConverter {

    private final MetricBuilder metricBuilder;

    public MetricConverter() {
        this.metricBuilder = MetricBuilder.getInstance();
    }

    public MetricConverter add(Metric metric) throws MetricConversionException {
        org.kairosdb.client.builder.Metric kairosMetric = metricBuilder.addMetric(metric.getName()).addDataPoint(metric.getTimestamp(), metric.getValue());

        kairosMetric.addTags(metric.getTags());
        return this;
    }


    public MetricBuilder convert() {
        return metricBuilder;
    }


}