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

package de.uniulm.omi.cloudiator.visor.telnet;

import de.uniulm.omi.cloudiator.visor.monitoring.MeasurementImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.MetricFactory;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Parses a line request into a metric.
 * <p/>
 * Expects the line to be of the format metricName, value, timestamp
 */
public class StringToMetricParser implements RequestParsingInterface<String, Metric> {

    private final MonitorContext monitorContext;

    public StringToMetricParser(MonitorContext monitorContext) {
        this.monitorContext = monitorContext;
    }

    @Override public Metric parse(String s) throws ParsingException {
        checkNotNull(s);

        final String[] parts = s.split(" ");
        if (parts.length != 3) {
            throw new ParsingException("Expected 3 strings, got " + parts.length);
        }
        final String metricName = parts[0];
        final String value = parts[1];
        long timestamp;
        try {
            timestamp = Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            throw new ParsingException("Could not convert third string to long.");
        }

        return MetricFactory
            .from(metricName, new MeasurementImpl(timestamp, value), monitorContext);
    }
}
