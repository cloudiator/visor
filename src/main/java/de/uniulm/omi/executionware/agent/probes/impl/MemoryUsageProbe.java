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

package de.uniulm.omi.executionware.agent.probes.impl;

import de.uniulm.omi.executionware.agent.probes.strategies.api.MemoryMeasurementStrategy;
import de.uniulm.omi.executionware.agent.metric.api.MetricNotAvailableException;
import de.uniulm.omi.executionware.agent.probes.api.Probe;

/**
 * The MemoryUsageProbe class.
 * <p>
 * Measures the current
 * ly used memory by the operating system in percentage.
 */
public class MemoryUsageProbe implements Probe {

    private final MemoryMeasurementStrategy<Double> memoryMeasurementStrategy;

    public MemoryUsageProbe(MemoryMeasurementStrategy<Double> memoryMeasurementStrategy) {
        this.memoryMeasurementStrategy = memoryMeasurementStrategy;
    }

    @Override
    public String getMetricName() {
        return "memory_usage_percentage";
    }

    /**
     * Measures the value of this metric.
     * <p>
     * It uses the com.sun.management.OperatingSystemMXBean to measure the total
     * physical memory and the free physical memory of the system.
     * It then calculates the consumed memory from those values.
     *
     * @return the currently consumed memory of the system in percent.
     * @throws de.uniulm.omi.executionware.agent.metric.api.MetricNotAvailableException if the measurement could not be completed as one of the memory measurements
     *                                                                         returned a negative value
     */
    @Override
    public Double getMetricValue() throws MetricNotAvailableException {
        return this.memoryMeasurementStrategy.measure();
    }

}
