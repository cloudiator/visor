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
 * Created by daniel on 27.01.16.
 */
public class MeasurementBuilder<E> {

    private long timestamp;
    private E value;
    private Map<String, String> tags;

    private MeasurementBuilder() {
        this.tags = new HashMap<>();
    }

    public static MeasurementBuilder<Object> newBuilder() {
        return new MeasurementBuilder<>();
    }

    public static <F> MeasurementBuilder<F> newBuilder(Class<F> fClass) {
        return new MeasurementBuilder<>();
    }

    public MeasurementBuilder<E> value(E value) {
        this.value = value;
        return this;
    }

    public MeasurementBuilder<E> timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public MeasurementBuilder<E> now() {
        timestamp = System.currentTimeMillis();
        return this;
    }

    public MeasurementBuilder<E> addTag(String key, String value) {
        this.tags.put(key, value);
        return this;
    }

    public MeasurementBuilder<E> addTags(Map<String, String> tags) {
        this.tags.putAll(tags);
        return this;
    }

    public Measurement<E> build() {
        return new MeasurementImpl<>(timestamp, value, tags);
    }


}
