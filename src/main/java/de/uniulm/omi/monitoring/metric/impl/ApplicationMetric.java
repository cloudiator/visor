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
 * The application metric class.
 * <p/>
 * Adds an application name to a server metric, defining the application
 * that reported the metric, meaning that this metric is application specific.
 */
public class ApplicationMetric extends ServerMetric {

    /**
     * Constructor of the application metric.
     *
     * @param name            the name of the metric.
     * @param value           the value of the metric.
     * @param timestamp       the timestamp when the metric was taken (unix format)
     * @param applicationName the name of the application reporting the metric.
     * @param Ip              the IP of the server, where the metric was measured.
     */
    public ApplicationMetric(String name, Object value, long timestamp, String applicationName, String Ip) {
        super(name, value, timestamp, Ip);
        this.applicationName = applicationName;
    }

    /**
     * The name of the application reporting the metric.
     */
    protected String applicationName;

    /**
     * Getter for the application name.
     *
     * @return the name of the application reporting the metric.
     */
    @KairosTag(name = "application")
    public String getApplicationName() {
        return applicationName;
    }


}
