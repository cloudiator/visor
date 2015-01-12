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

import de.uniulm.omi.executionware.agent.monitoring.metric.api.KairosTag;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class ServerMetric.
 * <p>
 * Represents a metric measured on a specific server. It extends the basic
 * metric by the ip address where the metric was taken.
 * <p>
 * Use MetricFactory to create metrics.
 *
 * @see de.uniulm.omi.executionware.agent.monitoring.metric.impl.MetricFactory
 */
public class ServerMetric extends Metric {

    /**
     * The ip address of the server where the metric was measured.
     */
    private final String ip;

    /**
     * Constructor for the server metric.
     *
     * @param name      the name of the metric.
     * @param value     the value of the metric.
     * @param timestamp the time where when the metric was measured (unix format)
     * @param ip        the ip of the server where the metric was measured.
     */
    ServerMetric(String name, Object value, long timestamp, String ip) {
        super(name, value, timestamp);
        checkNotNull(ip);
        checkArgument(!ip.isEmpty(), "Ip must not be empty");
        this.ip = ip;
    }

    /**
     * Getter for the ip.
     *
     * @return the ip of the server where the metric was measured.
     */
    @KairosTag(name = "server")
    public String getIp() {
        return this.ip;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Value: %s, Time: %s, Ip: %s", this.getName(), this.getValue(), this.getTimestamp(), this.getIp());
    }
}
