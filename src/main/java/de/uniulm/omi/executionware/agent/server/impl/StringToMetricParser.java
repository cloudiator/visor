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

package de.uniulm.omi.executionware.agent.server.impl;

import com.google.inject.Inject;
import de.uniulm.omi.executionware.agent.monitoring.metric.api.Metric;
import de.uniulm.omi.executionware.agent.monitoring.metric.api.MetricFactory;
import de.uniulm.omi.executionware.agent.server.api.ParsingException;
import de.uniulm.omi.executionware.agent.server.api.RequestParsingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 16.12.14.
 */
public class StringToMetricParser implements RequestParsingInterface<String, Metric> {

    private final MetricFactory metricFactory;
    private static final Logger logger = LogManager.getLogger(StringToMetricParser.class);

    @Inject
    public StringToMetricParser(MetricFactory metricFactory) {
        checkNotNull(metricFactory);
        this.metricFactory = metricFactory;
    }

    @Override
    public Metric parse(String s) throws ParsingException {
        checkNotNull(s);

        final String[] parts = s.split(" ");
        if (parts.length != 4) {
            throw new ParsingException("Expected 4 strings, got " + parts.length);
        }
        final String applicationName = parts[0];
        final String metricName = parts[1];
        final String value = parts[2];
        long timestamp;
        try {
            timestamp = Long.valueOf(parts[3]);
        } catch (NumberFormatException e) {
            throw new ParsingException("Could not convert 4th string to long.");
        }

        return this.metricFactory.from(metricName, value, timestamp, applicationName);
    }
}
