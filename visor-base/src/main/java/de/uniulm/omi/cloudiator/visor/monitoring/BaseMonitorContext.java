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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * Created by daniel on 21.01.15.
 */
public class BaseMonitorContext implements MonitorContext {

  private final Map<String, String> context;

  BaseMonitorContext(Map<String, String> context) {
    this.context = ImmutableMap.copyOf(context);
  }

  @Override
  public final String getValue(String context) {
    return getContext().get(context);
  }

  @Override
  public final boolean hasValue(String context) {
    return getContext().containsKey(context);
  }

  @Override
  public final String getOrDefault(String context, String defaultValue) {
    return getContext().getOrDefault(context, defaultValue);
  }

  @Override
  public Map<String, String> getContext() {
    return context;
  }

  @Override
  public final String toString() {
    return MoreObjects.toStringHelper(this.getClass()).add("context", getContext()).toString();
  }
}
