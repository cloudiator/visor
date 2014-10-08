/*
 *
 *  * Copyright (c) 2014 University of Ulm
 *  *
 *  * See the NOTICE file distributed with this work for additional information
 *  * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package de.uniulm.omi.monitoring.reporting.impl;

import de.uniulm.omi.monitoring.metric.api.KairosTag;
import org.kairosdb.client.builder.Metric;
import org.kairosdb.client.builder.MetricBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by daniel on 23.09.14.
 */
public class MetricConverter {

    private MetricBuilder metricBuilder;

    public MetricConverter() {
        this.metricBuilder = MetricBuilder.getInstance();
    }

    public MetricConverter add(de.uniulm.omi.monitoring.metric.impl.Metric metric) throws MetricConversionException {
        Metric kairosMetric = metricBuilder.addMetric(metric.getName()).addDataPoint(metric.getTimestamp(), metric.getValue());

        //we need to add the tags
        //fields
        for (Field field : metric.getClass().getFields()) {
            if (field.isAnnotationPresent(KairosTag.class)) {
                try {
                    kairosMetric.addTag(field.getAnnotation(KairosTag.class).name(), (String) field.get(metric));
                } catch (IllegalAccessException e) {
                    throw new MetricConversionException(String.format("Could not access field %s annotated with Tag", field.getName()),e);
                }
            }
        }

        //methods
        for (Method method : metric.getClass().getMethods()) {
            if (method.isAnnotationPresent(KairosTag.class)) {
                try {
                    kairosMetric.addTag(method.getAnnotation(KairosTag.class).name(), (String) method.invoke(metric));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new MetricConversionException(String.format("Could not access method %s annotated with Tag", method.getName()),e);
                }
            }
        }
        return this;
    }


    public MetricBuilder convert() {
        return metricBuilder;
    }


}
