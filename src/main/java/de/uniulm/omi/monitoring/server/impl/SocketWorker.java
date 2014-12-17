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


import de.uniulm.omi.monitoring.metric.impl.Metric;
import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;
import de.uniulm.omi.monitoring.reporting.impl.ReportingException;
import de.uniulm.omi.monitoring.server.api.ParsingException;
import de.uniulm.omi.monitoring.server.api.RequestParsingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by daniel on 15.12.14.
 */
public class SocketWorker implements Runnable {

    private final Socket socket;
    private final ReportingInterface<Metric> metricReporting;
    private final RequestParsingInterface<String, Metric> requestParser;
    private static final Logger logger = LogManager.getLogger(SocketWorker.class);

    SocketWorker(final Socket socket, ReportingInterface<Metric> metricReporting, RequestParsingInterface<String, Metric> requestParser) {
        this.socket = socket;
        this.metricReporting = metricReporting;
        this.requestParser = requestParser;
    }

    protected void closeSocket() {
        try {
            this.socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        logger.debug("New connection to server");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                this.socket.setSoTimeout(20 * 1000);
                Scanner in = new Scanner(this.socket.getInputStream());
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    Metric metric = this.requestParser.parse(line);
                    logger.debug("Server received new metric " + metric.getName());
                    this.metricReporting.report(metric);
                }
            } catch (IOException e) {
                logger.error(e);
            } catch (ParsingException e) {
                logger.error("Error parsing metric.", e);
            } catch (ReportingException e) {
                logger.error("Could not report metric.", e);
            } catch (Throwable t) {
                logger.fatal(t);
            } finally {
                logger.debug("Closing connection to server.");
                closeSocket();
                Thread.currentThread().interrupt();
            }
        }
    }
}
