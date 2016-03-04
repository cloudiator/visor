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

/**
 * Represents a measurement taken by a sensor.
 *
 * @author Daniel Baur
 */
public interface Measurement {

    /**
     * The timestamp the measurement object was taken.
     *
     * @return unix timestamp
     */
    long getTimestamp();

    /**
     * The value for this measurement at the defined timestamp.
     *
     * @return a object representing the value. mandatory
     */
    Object getValue();
}
