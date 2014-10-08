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

package de.uniulm.omi.monitoring.server.impl;

import de.uniulm.omi.monitoring.cli.CliOptions;
import de.uniulm.omi.monitoring.metric.impl.ApplicationMetric;
import de.uniulm.omi.monitoring.metric.impl.Metric;
import de.uniulm.omi.monitoring.server.api.RequestParser;

/**
 * Created by daniel on 08.10.14.
 */
public class MetricRequestParser implements RequestParser<Metric> {

    /**
     * Parses the given request, and creates an application metric from the request.
     *
     * @param request the request received from the server
     * @return an application specific metric representing the request.
     * @throws ParsingException if the request could not be parsed.
     */
    @Override
    public ApplicationMetric parseRequest(String request) throws ParsingException {
        // split the request at blanks
        String[] parts = request.split(" ");

        if (parts.length != 4) {
            throw new ParsingException("Could not parse request " + request);
        }

        String applicationName = parts[0];
        String metricName = parts[1];
        String value = parts[2];
        long timestamp = Long.valueOf(parts[3]);

        return new ApplicationMetric(metricName, value, timestamp, applicationName, CliOptions.getLocalIp());
    }
}
