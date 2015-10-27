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

import de.uniulm.omi.cloudiator.visor.config.ConfigurationException;
import de.uniulm.omi.cloudiator.visor.execution.ExecutionService;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import de.uniulm.omi.cloudiator.visor.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 15.12.14.
 */
public class TCPServer implements Server {

    private final String uuid;
    private static final Logger LOGGER = LogManager.getLogger(TCPServer.class);
    private final ExecutionService executionService;
    private final ReportingInterface<Metric> metricReporting;
    private final RequestParsingInterface<String, Metric> requestParser;
    private final int port;
    private final TCPServerListener listener;

    public TCPServer(ExecutionService executionService, ReportingInterface<Metric> metricReporting,
        RequestParsingInterface<String, Metric> requestParser, int port, String uuid) {

        checkArgument(port > 0, "Port must be > 0");
        if (port < 1024) {
            LOGGER.warn(
                "You are running the telnet server on a port < 1024. This is usually not a good idea.");
        }
        checkNotNull(uuid);
        checkArgument(!uuid.isEmpty());
        this.uuid = uuid;
        this.port = port;
        this.executionService = executionService;
        this.metricReporting = metricReporting;
        this.requestParser = requestParser;
        try {
            LOGGER.info(String.format("Starting tcp server on port %d", this.port));
            this.listener = new TCPServerListener(new ServerSocket(port));
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    @Override public String uuid() {
        return uuid;
    }

    @Override public int port() {
        return this.port;
    }

    @Override public MonitorContext getMonitorContext() {
        return requestParser.monitorContext();
    }

    @Override public void run() {
        listener.run();
    }

    private class TCPServerListener implements Runnable {

        private final ServerSocket serverSocket;

        private TCPServerListener(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket accept = this.serverSocket.accept();
                    LOGGER.info(accept.getInetAddress() + " opened a connection to the server.");
                    executionService.execute(new TCPSocketWorker(accept));
                } catch (IOException e) {
                    LOGGER.error("Error occurred while accepting connection.", e);
                }
            }
            try {
                this.serverSocket.close();
            } catch (IOException ignored) {
                LOGGER.warn(ignored);
            }
        }
    }


    private class TCPSocketWorker implements Runnable {

        private final Socket socket;

        private TCPSocketWorker(final Socket socket) {
            this.socket = socket;
        }

        protected void closeSocket() {
            try {
                this.socket.close();
            } catch (IOException ignored) {
                LOGGER.warn(ignored);
            }
        }

        @Override public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    this.socket.setSoTimeout(20 * 1000);
                    Scanner in = new Scanner(this.socket.getInputStream(), "UTF-8");
                    while (in.hasNextLine()) {
                        String line = in.nextLine();
                        LOGGER.debug("Server received new line " + line);
                        Metric metric = requestParser.parse(line);
                        LOGGER.debug("Server received new metric " + metric.getName());
                        metricReporting.report(metric);
                    }
                } catch (IOException e) {
                    LOGGER.error(e);
                } catch (ParsingException e) {
                    LOGGER.error("Error parsing metric.", e);
                } catch (ReportingException e) {
                    LOGGER.error("Could not report metric.", e);
                } finally {
                    LOGGER.debug("Closing connection from " + socket.getInetAddress());
                    closeSocket();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}


