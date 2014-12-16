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

package de.uniulm.omi.monitoring.metric.api;

import com.google.inject.ImplementedBy;
import de.uniulm.omi.monitoring.metric.impl.ApplicationMetric;
import de.uniulm.omi.monitoring.metric.impl.MetricFactory;
import de.uniulm.omi.monitoring.metric.impl.ServerMetric;

/**
 * Created by daniel on 15.12.14.
 */
@ImplementedBy(MetricFactory.class)
public interface MetricFactoryInterface {
    /**
     * Creates a metric from the given name and the value.
     * Automatically adds the local ip address and the timestamp.
     *
     * @param name  the name of the metric.
     * @param value the value of the metric.
     * @return a server specific metric having the name and the value given.
     */
    public ServerMetric from(String name, Object value);

    public ApplicationMetric from(String metricName, Object value, Long timestamp, String application);
}
