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

package de.uniulm.omi.cloudiator.visor.monitoring;

import java.util.Map;
import java.util.Optional;

/**
 * Created by daniel on 20.01.16.
 */
public interface SensorConfiguration {

  /**
   * Checks if the configuration has a value available for the given id.
   *
   * @param id the key
   * @return true if a value exists, false if not.
   * @throws NullPointerException if the id is null.
   */
  boolean hasValue(String id);

  /**
   * Returns an {@link Optional} value for the given id.
   *
   * @param id the key
   * @return {@link Optional#EMPTY} if no value exists, otherwise the value wrapped in an optional
   * object.
   */
  Optional<String> getValue(String id);

  /**
   * Returns an immutable Map of the complete configuration in key -> value format.
   *
   * @return an immutable map.
   */
  Map<String, String> getConfiguration();
}
