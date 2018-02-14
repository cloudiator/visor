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

public class MelodicDataFormat {

  private final static int level = 1;
  private final Object metricValue;
  private final String vmName;
  private final String cloudName;
  private final String componentName;
  private final long timestamp;


  MelodicDataFormat(Object metricValue, String vmName, String cloudName,
      String componentName, long timestamp) {
    this.metricValue = metricValue;
    this.vmName = vmName;
    this.cloudName = cloudName;
    this.componentName = componentName;
    this.timestamp = timestamp;
  }

  public Object getMetricValue() {
    return metricValue;
  }

  public String getVmName() {
    return vmName;
  }

  public String getCloudName() {
    return cloudName;
  }

  public String getComponentName() {
    return componentName;
  }

  public int getLevel() {
    return level;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
