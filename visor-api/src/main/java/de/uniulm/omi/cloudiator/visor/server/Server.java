/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.server;

import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;

import java.util.Map;

/**
 * Created by daniel on 23.10.15.
 */
public interface Server extends Runnable {

    /**
     * @return the port the server is running on.
     */
    int port();

    /**
     * Registers a new metric with this server.
     *
     * @param metricName     the name of the metric
     * @param monitorContext the context for this metric.
     */
    void registerMetric(String metricName, MonitorContext monitorContext);

    /**
     * Unregisters a new metric.
     *
     * @param metricName the name of the metric.
     */
    void unregisterMetric(String metricName);

    /**
     * The context objects this server uses for its metrics.
     * <p/>
     * Note:
     * <p/>
     * immutable, use registerMetric and unregisterMetric methods
     * for modification.
     *
     * @return the context the server uses for the metrics
     */
    Map<String, MonitorContext> metricContext();
}
