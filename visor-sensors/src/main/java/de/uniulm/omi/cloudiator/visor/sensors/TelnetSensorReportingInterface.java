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

package de.uniulm.omi.cloudiator.visor.sensors;

import com.google.inject.Singleton;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by daniel on 24.11.15.
 */
@Singleton public class TelnetSensorReportingInterface implements ReportingInterface<Metric> {

    private final LinkedBlockingQueue<Metric> buffer;

    public TelnetSensorReportingInterface() {
        buffer = new LinkedBlockingQueue<>();
    }

    @Override public void report(Metric item) throws ReportingException {
        this.buffer.add(item);
    }

    @Override public void report(Collection<Metric> items) throws ReportingException {
        this.buffer.addAll(items);
    }

    LinkedBlockingQueue<Metric> getBuffer() {
        return buffer;
    }
}
