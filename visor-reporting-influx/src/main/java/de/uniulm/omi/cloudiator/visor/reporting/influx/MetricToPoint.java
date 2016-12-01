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

package de.uniulm.omi.cloudiator.visor.reporting.influx;

import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import org.influxdb.dto.Point;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by daniel on 01.12.16.
 */
public class MetricToPoint implements Function<Metric, Point> {

    @Override public Point apply(Metric metric) {

        Point.Builder builder =
            Point.measurement(metric.getName()).time(metric.getTimestamp(), TimeUnit.MILLISECONDS)
                .addField("value", Double.valueOf(metric.getValue().toString()));
        metric.getTags().forEach(builder::addField);
        return builder.build();
    }

}
