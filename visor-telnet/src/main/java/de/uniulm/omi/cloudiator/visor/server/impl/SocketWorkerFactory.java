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

package de.uniulm.omi.cloudiator.visor.server.impl;

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.visor.monitoring.api.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.api.ReportingInterface;
import de.uniulm.omi.cloudiator.visor.reporting.modules.api.QueuedReporting;
import de.uniulm.omi.cloudiator.visor.server.api.RequestParsingInterface;
import de.uniulm.omi.cloudiator.visor.server.api.SocketWorkerFactoryInterface;

import java.net.Socket;

/**
 * Created by daniel on 16.12.14.
 */
public class SocketWorkerFactory implements SocketWorkerFactoryInterface {


    private final ReportingInterface<Metric> metricReporting;
    private final RequestParsingInterface<String, Metric> requestParser;

    @Inject
    public SocketWorkerFactory(@QueuedReporting ReportingInterface<Metric> metricReporting, RequestParsingInterface<String, Metric> requestParser) {
        this.metricReporting = metricReporting;
        this.requestParser = requestParser;
    }

    @Override
    public SocketWorker create(Socket socket) {
        return new SocketWorker(socket, metricReporting, requestParser);
    }
}
