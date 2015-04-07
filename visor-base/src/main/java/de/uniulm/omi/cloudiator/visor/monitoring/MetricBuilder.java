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

import com.google.common.collect.ImmutableMap;
import de.uniulm.omi.cloudiator.visor.monitoring.api.Metric;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 21.01.15.
 */
public class MetricBuilder {

    private String name;
    private Object value;
    private long timestamp;
    private final Map<String, String> tags;

    MetricBuilder() {
        tags = new HashMap<>();
    }

    public static MetricBuilder newBuilder() {
        return new MetricBuilder();
    }

    public MetricBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MetricBuilder value(Object value) {
        this.value = value;
        return this;
    }

    public MetricBuilder timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public MetricBuilder addTag(String key, String value) {
        this.tags.put(key, value);
        return this;
    }

    public MetricBuilder addTags(Map<String, String> tags) {
        this.tags.putAll(tags);
        return this;
    }

    public Metric build() {
        return new MetricImpl(name, value, timestamp, ImmutableMap.copyOf(this.tags));
    }
}
