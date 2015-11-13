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

import de.uniulm.omi.cloudiator.visor.execution.ExecutionService;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;

/**
 * Created by daniel on 11.11.15.
 */
public class TCPServerBuilder {

    private int port;
    private ExecutionService executionService;
    private ReportingInterface<Metric> metricReporting;

    public static TCPServerBuilder newBuilder() {
        return new TCPServerBuilder();
    }

    private TCPServerBuilder() {

    }

    /**
     * @param port the port the server should listen to
     * @return fluent interface
     */
    public TCPServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    /**
     * @param executionService the executionservice the server should use for creating its worker threads.
     * @return fluent interface
     */
    public TCPServerBuilder executionService(ExecutionService executionService) {
        this.executionService = executionService;
        return this;
    }

    /**
     * @param reportingInterface the interface where this server should report its metrics.
     * @return fluent interface.
     */
    public TCPServerBuilder reportingInterface(ReportingInterface<Metric> reportingInterface) {
        this.metricReporting = reportingInterface;
        return this;
    }

    public TCPServer build() {
        return new TCPServer(executionService, metricReporting, port);
    }


}
