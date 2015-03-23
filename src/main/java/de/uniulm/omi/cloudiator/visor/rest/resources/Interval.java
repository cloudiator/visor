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

package de.uniulm.omi.cloudiator.visor.rest.resources;

/**
 * Created by daniel on 09.02.15.
 */
public class Interval {

    private String timeUnit;

    private long period;

    Interval() {
    }

    Interval(long period, String timeUnit) {
        this.period = period;
        this.timeUnit = timeUnit;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getPeriod() {
        return period;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setPeriod(long period) {
        this.period = period;
    }
}
