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

package de.uniulm.omi.cloudiator.visor.reporting.chukwa;

import de.uniulm.omi.cloudiator.visor.monitoring.Metric;

import java.util.function.Function;

/**
 * Created by daniel on 23.02.16.
 */
public class MetricToChukwa implements Function<Metric, String> {

    private final String vmUuid;

    public MetricToChukwa(String vmUuid) {
        this.vmUuid = vmUuid;
    }

    @Override public String apply(Metric metric) {
        return "VMID" +
            "\t" +
            metric.getName() +
            "\t" +
            "timestamp" +
            "\n" +
            vmUuid +
            "\t" +
            metric.getValue() +
            "\t" +
            metric.getTimestamp();
    }
}
