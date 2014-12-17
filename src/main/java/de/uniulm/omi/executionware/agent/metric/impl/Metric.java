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

package de.uniulm.omi.executionware.agent.metric.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A basic metric for the system.
 * <p>
 * A metric always consists of:
 * - a name: the name of the matric
 * - a value: a value for this metric.
 * - a timestamp: the unix timestamp when this metric was taken.
 */
public abstract class Metric {

    /**
     * The name of the metric.
     */
    protected final String name;

    /**
     * The value of the metric.
     */
    protected final Object value;

    /**
     * The time the metric was taken as unix timestamp.
     */
    protected final long timestamp;

    /**
     * Constructor for the metric.
     *
     * @param name      the name of the metric.
     * @param value     the value of the metric.
     * @param timestamp the timestamp of the metric.
     */
    Metric(String name, Object value, long timestamp) {

        checkNotNull(name);
        checkNotNull(value);
        checkNotNull(timestamp);

        checkArgument(!name.isEmpty(), "Name must not be empty.");
        checkArgument(timestamp > 0, "Timestamp must be > 0");

        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
    }

    /**
     * Getter for the name of the metric.
     *
     * @return the name of the metric.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the value of the metric.
     *
     * @return the value of the metric.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Getter for the timestamp of the metric.
     *
     * @return the time the metric was taken.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * To String method for the metric.
     *
     * @return the metric as string representation, mainly for logging purposes.
     */
    public String toString() {
        return String.format("Name: %s, Value: %s, Time: %s", this.getName(), this.getValue(), this.getTimestamp());
    }

}
