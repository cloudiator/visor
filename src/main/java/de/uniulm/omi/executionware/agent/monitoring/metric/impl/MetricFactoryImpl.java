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

package de.uniulm.omi.executionware.agent.monitoring.metric.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.executionware.agent.monitoring.metric.api.Metric;
import de.uniulm.omi.executionware.agent.monitoring.metric.api.MetricFactory;
import de.uniulm.omi.executionware.agent.monitoring.monitors.impl.MonitorContext;
import de.uniulm.omi.executionware.agent.monitoring.sensors.api.Measurement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class MetricFactory.
 * <p>
 * Used for creating metric objects.
 */
public class MetricFactoryImpl implements MetricFactory {

    private final String localIp;

    /**
     * Constructor.
     *
     * @param localIp the ip of the machine
     */
    @Inject
    public MetricFactoryImpl(@Named("localIp") String localIp) {
        checkNotNull(localIp);
        this.localIp = localIp;
    }


    @Override
    public Metric from(String metricName, Measurement measurement) {
        return MetricBuilder
                .create()
                .name(metricName)
                .timestamp(measurement.getTimestamp())
                .value(measurement.getValue())
                .addTag("server", localIp)
                .build();
    }

    @Override
    public Metric from(String metricName, Object value, Long timestamp, String application) {
        return MetricBuilder
                .create()
                .name(metricName)
                .timestamp(timestamp)
                .value(value)
                .addTag("server", localIp)
                .addTag("application", application)
                .build();
    }

    @Override
    public Metric from(String metricName, Measurement measurement, MonitorContext monitorContext) {
        return MetricBuilder.create()
                .name(metricName)
                .timestamp(measurement.getTimestamp())
                .value(measurement.getValue())
                .addTag("server", localIp)
                .addTag(monitorContext.getContext(), monitorContext.getValue())
                .build();
    }
}
