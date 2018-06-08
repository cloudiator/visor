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

package de.uniulm.omi.cloudiator.visor.monitoring;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

/**
 * A basic metric for the system. <p> A metric always consists of: - a name: the name of the metric
 * - a value: a value for this metric. - a timestamp: the unix timestamp when this metric was taken.
 * - a map of key value tags <p> <p> Use MetricFactory to create metrics.
 *
 * @see MetricFactory
 */
public class MetricImpl implements Metric {

  private final Monitor monitor;

  private final Measurement<?> measurement;

  /**
   * Constructor for the metric.
   *
   * @param monitor the monitor collecting this metric
   * @param measurement the measurement represented by this metric.
   */
  MetricImpl(Monitor monitor, Measurement<?> measurement) {

    checkNotNull(monitor, "monitor is null");
    checkNotNull(measurement, "measurement is null");

    this.monitor = monitor;
    this.measurement = measurement;
  }

  /**
   * Getter for the name of the metric.
   *
   * @return the name of the metric.
   */
  @Override
  public String getName() {
    return monitor.metricName();
  }

  /**
   * Getter for the value of the metric.
   *
   * @return the value of the metric.
   */
  @Override
  public Object getValue() {
    return measurement.getValue();
  }

  /**
   * Getter for the timestamp of the metric.
   *
   * @return the time the metric was taken.
   */
  @Override
  public long getTimestamp() {
    return measurement.getTimestamp();
  }

  @Override
  public Monitor monitor() {
    return this.monitor;
  }

  /**
   * Getter for the tags of the metric.
   *
   * todo: check if we need to merge component ID here.
   *
   * @return tags for the metric.
   */
  @Override
  public Map<String, String> getTags() {
    return monitor.monitorContext().getContext();
  }

  /**
   * To String method for the metric.
   *
   * @return the metric as string representation, mainly for logging purposes.
   */
  @Override
  public String toString() {
    StringBuilder tagsString = new StringBuilder("[");
    for (Map.Entry<String, String> mapEntry : this.getTags().entrySet()) {
      tagsString.append(mapEntry.getKey());
      tagsString.append(": ");
      tagsString.append(mapEntry.getValue());
      tagsString.append(",");
    }
    tagsString.append("]");
    return String.format("Metric(Name: %s, Value: %s, Time: %s, Tags: %s)", this.getName(),
        this.getValue(), this.getTimestamp(), tagsString.toString());
  }
}
