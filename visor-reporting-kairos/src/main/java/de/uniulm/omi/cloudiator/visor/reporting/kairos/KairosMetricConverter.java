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

package de.uniulm.omi.cloudiator.visor.reporting.kairos;


import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import java.util.Map;
import org.kairosdb.client.builder.MetricBuilder;

/**
 * Created by daniel on 23.09.14.
 */
public class KairosMetricConverter {

  private final MetricBuilder metricBuilder;

  public KairosMetricConverter() {
    this.metricBuilder = MetricBuilder.getInstance();
  }

  public KairosMetricConverter add(Metric metric) throws KairosMetricConversionException {
    org.kairosdb.client.builder.Metric kairosMetric = metricBuilder.addMetric(metric.getName())
        .addDataPoint(metric.getTimestamp(), metric.getValue());

    //workaround for https://github.com/kairosdb/kairosdb-client/issues/27
    //manually add single tags as addTags() is broken.
    for (final Map.Entry<String, String> entry : metric.getTags().entrySet()) {
      kairosMetric.addTag(entry.getKey(), entry.getValue());
    }
    return this;
  }


  public MetricBuilder convert() {
    return metricBuilder;
  }


}
