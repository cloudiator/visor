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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import de.uniulm.omi.cloudiator.visor.execution.ExecutionService;
import de.uniulm.omi.cloudiator.visor.monitoring.MeasurementBuilder;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.MetricFactory;
import de.uniulm.omi.cloudiator.visor.monitoring.Monitor;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import de.uniulm.omi.cloudiator.visor.server.Server;
import de.uniulm.omi.cloudiator.visor.server.ServerRegistry;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daniel on 15.12.14.
 */
public class TCPServer implements Server {

  private static final Logger LOGGER = LoggerFactory.getLogger(TCPServer.class);
  private final ExecutionService executionService;
  private final ReportingInterface<Metric> metricReporting;
  private final ServerRegistry serverRegistry;
  private final int port;
  private final TCPServerListener listener;
  private Map<String, Monitor> monitors;

  /**
   * Creates a new tcp server. <p> Use {@link TCPServerBuilder} to create instances.
   *
   * @param executionService the {@link ExecutionService} used for worker threads.
   * @param metricReporting the {@link ReportingInterface} use for reporting the metrics
   * @param port the port the server listens to.
   * @param serverRegistry the server registry where this server is registered
   */
  TCPServer(ExecutionService executionService, ReportingInterface<Metric> metricReporting,
      int port, ServerRegistry serverRegistry) throws IOException {

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
    checkNotNull(serverRegistry);
    this.serverRegistry = serverRegistry;
    this.monitors = new ConcurrentHashMap<>();

    LOGGER.info(String.format("Starting tcp server on port %d", this.port));

    this.listener = new TCPServerListener(new ServerSocket(port));

    this.executionService.execute(this);
  }

  @Override
  public int port() {
    return this.port;
  }

  @Override
  public void registerMonitor(String metricName, Monitor monitor) {
    monitors.put(metricName, monitor);
  }

  @Override
  public void unregisterMonitor(String metricName) {
    monitors.remove(metricName);
    if (this.monitors.isEmpty()) {
      LOGGER.info(
          String.format("Last metric was unregistered from server %s. Shutting down.", this));
      serverRegistry.unregister(this);
      listener.stop();
    }
  }

  @Override
  public void run() {
    listener.run();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("port", port).toString();
  }

  private class TCPServerListener implements Runnable {

    private final ServerSocket serverSocket;
    private final List<TCPSocketWorker> workers;

    private TCPServerListener(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
      this.workers = new ArrayList<>();
    }

    private void stop() {
      workers.forEach(TCPSocketWorker::stop);
      Thread.currentThread().interrupt();
      try {
        serverSocket.close();
      } catch (IOException ignored) {
        LOGGER.warn("Ignoring exception thrown during stopping the server", ignored);
      }
    }

    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted() && !serverSocket.isClosed()) {
        try {
          Socket accept = this.serverSocket.accept();
          LOGGER.info(accept.getInetAddress() + " opened a connection to the server.");
          TCPSocketWorker tcpSocketWorker = new TCPSocketWorker(accept, workers::remove);
          workers.add(tcpSocketWorker);
          executionService.execute(tcpSocketWorker);
        } catch (IOException e) {
          LOGGER.error("Exception occurred while accepting the socket.", e);
        }
      }
    }


    private class TCPSocketWorker implements Runnable {

      private final Socket socket;
      private final StringToMetricParser stringToMetricParser;
      private final Consumer<TCPSocketWorker> callback;


      private TCPSocketWorker(final Socket socket, final Consumer<TCPSocketWorker> callback) {
        this.socket = socket;
        this.stringToMetricParser = new StringToMetricParser();
        this.callback = callback;
      }

      protected void closeSocket() {
        try {
          this.socket.close();
        } catch (IOException ignored) {
          LOGGER.warn("Ignoring exception during closing of socket.", ignored);
        }
      }

      private void stop() {
        Thread.currentThread().interrupt();
      }

      @Override
      public void run() {

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
            LOGGER.error("Error reading socket", e);
          } catch (ParsingException e) {
            LOGGER.error("Error parsing metric.", e);
          } catch (ReportingException e) {
            LOGGER.error("Could not report metric.", e);
          } finally {
            LOGGER.debug("Closing connection from " + socket.getInetAddress());
            Thread.currentThread().interrupt();
          }
        }
        LOGGER.debug(String.format("%s got interrupted, closing socket", this));
        closeSocket();
        callback.accept(this);
      }

      @Override
      public String toString() {
        return MoreObjects.toStringHelper(this).add("InetAddress", socket.getInetAddress())
            .toString();
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

        if (!monitors.containsKey(metricName)) {
          throw new ParsingException(
              String.format("Metric %s is unknown to this server.", metricName));
        }
        Monitor monitor = monitors.get(metricName);

        return MetricFactory.from(metricName,
            MeasurementBuilder.newBuilder(String.class).timestamp(timestamp).value(value)
                .build(), monitor.monitorContext().getContext(), monitor.componentId());
      }
    }
  }
}


