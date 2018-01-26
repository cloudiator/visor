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

import static de.uniulm.omi.cloudiator.visor.config.ContextConstants.COMPONENT_ID;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Created by daniel on 06.02.15.
 */
public class MetricFactory {



  private MetricFactory() {

  }

  public static Metric from(String metricName, Measurement<?> measurement,
      Map<String, String> tags, @Nullable String componentId) {

    Map<String, String> mergedTags = new HashMap<>(measurement.tags().size() + tags.size());
    mergedTags.putAll(tags);
    mergedTags.putAll(measurement.tags());
    if (componentId != null) {
      mergedTags.put(COMPONENT_ID, componentId);
    }

    return MetricBuilder.newBuilder().name(metricName).value(measurement.getValue())
        .timestamp(measurement.getTimestamp()).addTags(mergedTags).build();
  }

}
