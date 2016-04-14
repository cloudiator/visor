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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 20.01.16.
 */
public class SensorConfigurationBuilder {

    private Map<String, String> configuration;

    public SensorConfigurationBuilder() {
        this.configuration = new HashMap<>();
    }

    public static SensorConfigurationBuilder newBuilder() {
        return new SensorConfigurationBuilder();
    }

    public SensorConfigurationBuilder addValue(String key, String value) {
        this.configuration.put(key, value);
        return this;
    }

    public SensorConfigurationBuilder addValues(Map<String, String> valueMap) {
        this.configuration.putAll(valueMap);
        return this;
    }

    public SensorConfiguration build() {
        return new SensorConfigurationImpl(configuration);
    }
}
