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

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A basic metric for the system.
 * <p>
 * A metric always consists of:
 * - a name: the name of the metric
 * - a value: a value for this metric.
 * - a timestamp: the unix timestamp when this metric was taken.
 * - a map of key value tags
 * <p>
 * <p>
 * Use MetricFactory to create metrics.
 *
 * @see de.uniulm.omi.executionware.agent.monitoring.metric.api.MetricFactory
 */
public class MetricImpl implements de.uniulm.omi.executionware.agent.monitoring.metric.api.Metric {

    protected final String name;

    protected final Object value;

    protected final long timestamp;

    protected final Map<String, String> tags;

    /**
     * Constructor for the metric.
     *
     * @param name      the name of the metric.
     * @param value     the value of the metric.
     * @param timestamp the timestamp of the metric.
     * @param tags      tags for the metric.
     */
    MetricImpl(String name, Object value, long timestamp, Map<String, String> tags) {

        checkNotNull(name);
        checkNotNull(value);
        checkNotNull(timestamp);
        checkNotNull(tags);

        checkArgument(!name.isEmpty(), "Name must not be empty.");
        checkArgument(timestamp > 0, "Timestamp must be > 0");

        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    /**
     * Getter for the name of the metric.
     *
     * @return the name of the metric.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Getter for the value of the metric.
     *
     * @return the value of the metric.
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * Getter for the timestamp of the metric.
     *
     * @return the time the metric was taken.
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for the tags of the metric.
     *
     * @return tags for the metric.
     */
    @Override
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * To String method for the metric.
     *
     * @return the metric as string representation, mainly for logging purposes.
     */
    public String toString() {
        StringBuilder tags = new StringBuilder("[");
        for (Map.Entry<String, String> mapEntry : this.getTags().entrySet()) {
            tags.append(mapEntry.getKey());
            tags.append(": ");
            tags.append(mapEntry.getValue());
            tags.append(",");
        }
        tags.append("]");
        return String.format("Metric(Name: %s, Value: %s, Time: %s, Tags: %s)", this.getName(), this.getValue(), this.getTimestamp(), tags.toString());
    }

}
