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

import de.uniulm.omi.monitoring.cli.CliOptions;

/**
 * A helper class for creating metrics.
 * <p/>
 * Offers methods to created metrics from different other representations.
 */
public class MetricFactory {

    /**
     * Singleton instance.
     */
    private static MetricFactory instance = new MetricFactory();

    /**
     * Private constructor for singleton pattern.
     */
    private MetricFactory() {

    }

    /**
     * Returns instance of the metric factory.
     * Implementation of the singleton pattern.
     *
     * @return unique instance of metric factory.
     */
    public static MetricFactory getInstance() {
        return instance;
    }

    /**
     * Creates a metric from the given name and the value.
     * Automatically adds the local ip address and the timestamp.
     *
     * @param name  the name of the metric.
     * @param value the value of the metric.
     * @return a server specific metric having the name and the value given.
     */
    public ServerMetric fromNameAndValue(String name, Object value) {
        return new ServerMetric(name, value, System.currentTimeMillis(), CliOptions.getLocalIp());
    }
}
