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

package de.uniulm.omi.cloudiator.visor.rest.converters;

import de.uniulm.omi.cloudiator.visor.monitoring.Monitor;
import de.uniulm.omi.cloudiator.visor.monitoring.PushMonitorImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorMonitorImpl;
import de.uniulm.omi.cloudiator.visor.rest.entities.MonitorDto;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by daniel on 11.11.15.
 */
public class MonitorConverters {

  private static Map<Class<? extends Monitor>, Function<? extends Monitor, ? extends MonitorDto>>
      converters = new HashMap<>();

  static {
    converters.put(SensorMonitorImpl.class, new SensorMonitorConverter());
    converters.put(PushMonitorImpl.class, new PushMonitorConverter());
  }

  public static Function<Monitor, MonitorDto> getConverter(Class<? extends Monitor> fromClass) {

    if (!converters.containsKey(fromClass)) {
      throw new IllegalArgumentException();
    }

    //noinspection unchecked
    return (Function<Monitor, MonitorDto>) converters.get(fromClass);
  }

}
