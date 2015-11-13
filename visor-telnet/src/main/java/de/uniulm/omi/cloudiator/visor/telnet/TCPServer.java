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
import de.uniulm.omi.cloudiator.visor.monitoring.MeasurementImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.MetricFactory;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import de.uniulm.omi.cloudiator.visor.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 15.12.14.
 */
public class TCPServer implements Server {

    private static final Logger LOGGER = LogManager.getLogger(TCPServer.class);
    private final ExecutionService executionService;
    private final ReportingInterface<Metric> metricReporting;
    private Map<String, MonitorContext> metricContext;
    private final int port;
    private final TCPServerListener listener;

    /**
     * Creates a new tcp server.
     * <p/>
     * Use {@link TCPServerBuilder} to create instances.
     *
     * @param executionService the {@link ExecutionService} used for worker threads.
     * @param metricReporting  the {@link ReportingInterface} use for reporting the metrics
     * @param port             the port the server listens to.
     */
    TCPServer(ExecutionService executionService, ReportingInterface<Metric> metricReporting,
        int port) {

        checkArgument(port > 0, "Port must be > 0");
        if (port < 1024) {
            LOGGER.warn(
                "You are running the telnet server on a port < 1024. This is usually not a good idea.");
        }
        this.port = port;
        checkNotNull(executionService);
        this.executionService = executionService;
        checkNotNull(metricReporting);
        this.metricReporting = metricReporting;
        this.metricContext = new ConcurrentHashMap<>();
        try {
            LOGGER.info(String.format("Starting tcp server on port %d", this.port));
            this.listener = new TCPServerListener(new ServerSocket(port));
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
        this.executionService.execute(this);
    }

    @Override public int port() {
        return this.port;
    }

    @Override public void registerMetric(String metricName, MonitorContext monitorContext) {
        metricContext.put(metricName, monitorContext);
    }

    @Override public void unregisterMetric(String metricName) {
        metricContext.remove(metricName);
    }

    @Override public Map<String, MonitorContext> metricContext() {
        return null;
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
        private final StringToMetricParser stringToMetricParser;


        private TCPSocketWorker(final Socket socket) {
            this.socket = socket;
            this.stringToMetricParser = new StringToMetricParser();
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
                        Metric metric = stringToMetricParser.parse(line);
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


    private class StringToMetricParser {

        public Metric parse(String s) throws ParsingException {
            checkNotNull(s);

            final String[] parts = s.split(" ");
            if (parts.length != 3) {
                throw new ParsingException(String
                    .format("Expected line %s to have exactly three parts, has %s", s,
                        parts.length));
            }
            final String metricName = parts[0];
            final String value = parts[1];
            long timestamp;
            try {
                timestamp = Long.parseLong(parts[2]);
            } catch (NumberFormatException e) {
                throw new ParsingException(String.format(
                    "Expected third string of line %s to be a timestamp but could not convert to long",
                    s), e);
            }

            if (!metricContext.containsKey(metricName)) {
                throw new ParsingException(
                    String.format("Metric %s is unknown to this server.", metricName));
            }
            MonitorContext monitorContext = metricContext.get(metricName);

            return MetricFactory.from(metricName, new MeasurementImpl(timestamp, value),
                monitorContext.getContext());
        }
    }
}


