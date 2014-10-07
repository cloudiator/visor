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

package de.uniulm.omi.monitoring.server;

import de.uniulm.omi.monitoring.metric.impl.MetricFactory;
import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;
import de.uniulm.omi.monitoring.reporting.impl.MetricReportingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by daniel on 22.09.14.
 */
public class ServerWorker implements Runnable {

    private InputStream inputStream;
    private ReportingInterface metricReportingInterface;

    private static final Logger logger = LogManager.getLogger(ServerWorker.class);

    public ServerWorker(InputStream inputStream, ReportingInterface metricReportingInterface) {
        this.inputStream = inputStream;
        this.metricReportingInterface = metricReportingInterface;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Scanner in = new Scanner(this.inputStream);
            while (in.hasNextLine()) {
                try {
                    this.metricReportingInterface.report(MetricFactory.getInstance().fromRequest(in.nextLine()));
                } catch (IllegalRequestException e) {
                    logger.error("Illegal request",e);
                } catch (MetricReportingException e) {
                    logger.error("Could not report metric.",e);
                }
            }
        }
    }
}
