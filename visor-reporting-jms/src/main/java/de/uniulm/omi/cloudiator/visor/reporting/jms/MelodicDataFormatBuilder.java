/*
 * Copyright (c) 2014-2018 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.reporting.jms;

public class MelodicDataFormatBuilder {

  private Object metricValue;
  private String vmName;
  private String cloudName;
  private String componentName;
  private long timestamp;

  private MelodicDataFormatBuilder() {

  }

  public static MelodicDataFormatBuilder create() {
    return new MelodicDataFormatBuilder();
  }

  public MelodicDataFormatBuilder setMetricValue(Object metricValue) {
    this.metricValue = metricValue;
    return this;
  }

  public MelodicDataFormatBuilder setVmName(String vmName) {
    this.vmName = vmName;
    return this;
  }

  public MelodicDataFormatBuilder setCloudName(String cloudName) {
    this.cloudName = cloudName;
    return this;
  }

  public MelodicDataFormatBuilder setComponentName(String componentName) {
    this.componentName = componentName;
    return this;
  }

  public MelodicDataFormatBuilder setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public MelodicDataFormat build() {
    return new MelodicDataFormat(metricValue, vmName, cloudName, componentName, timestamp);
  }
}
