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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.monitoring.metric.api.MetricFactoryInterface;

import static com.google.common.base.Preconditions.checkNotNull;

public class MetricFactory implements MetricFactoryInterface {

    private final String localIp;

    @Inject
    public MetricFactory(@Named("localIp") String localIp) {
        checkNotNull(localIp);
        this.localIp = localIp;
    }

    @Override
    public ServerMetric from(String metricName, Object value) {
        return new ServerMetric(metricName, value, System.currentTimeMillis(), this.localIp);
    }

    @Override
    public ApplicationMetric from(String metricName, Object value, Long timestamp, String application) {
        return new ApplicationMetric(metricName, value, timestamp, application, this.localIp);
    }
}
