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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 21.01.15.
 */
public class DefaultMonitorContext implements MonitorContext {

    public static final String LOCAL_IP = "local_ip";
    private final Map<String, String> context;

    public static MonitorContextBuilder builder() {
        return new MonitorContextBuilder();
    }

    private DefaultMonitorContext(Map<String, String> context) {
        this.context = context;
    }

    @Override public String getValue(String context) {
        return this.context.get(context);
    }

    @Override public boolean hasValue(String context) {
        return this.context.containsKey(context);
    }

    @Override public String getOrDefault(String context, String defaultValue) {
        return this.context.getOrDefault(context, defaultValue);
    }

    @Override public Map<String, String> getContext() {
        return context;
    }

    @Override public String toString() {
        StringBuilder contextString = new StringBuilder("[");
        for (Map.Entry<String, String> mapEntry : this.getContext().entrySet()) {
            contextString.append(mapEntry.getKey());
            contextString.append(": ");
            contextString.append(mapEntry.getValue());
            contextString.append(",");
        }
        contextString.append("]");
        return String.format("MonitorContext(Context: %s)", contextString.toString());
    }

    public static class MonitorContextBuilder {

        private final Map<String, String> map;

        public MonitorContextBuilder() {
            map = new HashMap<>();
        }

        public MonitorContextBuilder addContext(String context, String value) {
            this.map.put(context, value);
            return this;
        }

        public MonitorContextBuilder addContext(Map<String, String> context) {
            this.map.putAll(context);
            return this;
        }

        public MonitorContext build() {
            return new DefaultMonitorContext(ImmutableMap.copyOf(this.map));
        }
    }

}
