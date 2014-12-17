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

package de.uniulm.omi.monitoring.metric.impl;

import de.uniulm.omi.monitoring.metric.api.KairosTag;

/**
 * The class ServerMetric.
 * <p/>
 * Represents a metric measured on a specific server. It extends the basic
 * metric by the ip address where the metric was taken.
 */
public class ServerMetric extends Metric {

    /**
     * The ip address of the server where the metric was measured.
     */
    protected final String ip;

    /**
     * Constructor for the server metric.
     *
     * @param name      the name of the metric.
     * @param value     the value of the metric.
     * @param timestamp the time where when the metric was measured (unix format)
     * @param ip        the ip of the server where the metric was measured.
     */
    public ServerMetric(String name, Object value, long timestamp, String ip) {
        super(name, value, timestamp);
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
}
