/*
 * Copyright (c) 2014-2016 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.reporting.druid;

import com.google.common.collect.ImmutableMap;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by daniel on 07.09.16.
 */
public class DruidMetricConverter implements Function<Metric, Map<String, Object>> {

    @Override public Map<String, Object> apply(Metric metric) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder();



        builder.put("timestamp", new DateTime(metric.getTimestamp()).toString());
        builder.put("name", metric.getName());
        builder.put("value", metric.getValue());
        //builder.putAll(metric.getTags());

        return builder.build();
    }

}
