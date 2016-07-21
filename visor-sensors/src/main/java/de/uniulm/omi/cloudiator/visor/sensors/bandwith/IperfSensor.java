/*
 * Copyright (c) 2014-2016 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.sensors.bandwith;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HostAndPort;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.AbstractSensor;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;
import org.apache.commons.exec.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.StreamSupport;


/**
 * Created by daniel on 20.07.16.
 */
public class IperfSensor extends AbstractSensor<Double> {

    private static final String HOST_FIELD = "host";
    private static final String HOST_DEFAULT_VALUE = "localhost";
    private HostAndPort host;


    private static class Iperf3Installer {

        boolean installed() {
            CommandLine which = new CommandLine("which");
            which.addArgument("iperf3");
            Executor whichExecutor = new DefaultExecutor();
            whichExecutor.setExitValue(0);
            try {
                whichExecutor.execute(which);
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        void install() throws IOException {
            CommandLine install = new CommandLine("sudo");
            install.addArgument("apt-get");
            install.addArgument("install");
            install.addArgument("iperf3");
            Executor installExecutor = new DefaultExecutor();
            installExecutor.setExitValue(0);
            installExecutor.execute(install);
        }
    }


    private static class IperfServer {

        void start() throws IOException {
            CommandLine startServer = new CommandLine("iperf3");
            startServer.addArgument("-sD");
            Executor start = new DefaultExecutor();
            start.execute(startServer);
        }

    }


    private static class IperfClient {

        private final CommandLine command;

        private IperfClient(HostAndPort hostAndPort) {
            command = new CommandLine("iperf3");
            command.addArgument("-c");
            command.addArgument(hostAndPort.getHostText());
            command.addArgument("--json");
        }

        <E> E measure(Function<String, E> parser) throws MeasurementNotAvailableException {
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ExecuteStreamHandler streamHandler = new PumpStreamHandler(outputStream);

            ExecuteWatchdog watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
            Executor executor = new DefaultExecutor();
            executor.setExitValue(0);
            executor.setWatchdog(watchdog);
            executor.setStreamHandler(streamHandler);

            try {
                executor.execute(command, resultHandler);
                resultHandler.waitFor();
                String result = outputStream.toString();
                return parser.apply(result);
            } catch (InterruptedException | IOException | IllegalArgumentException e) {
                throw new MeasurementNotAvailableException(e);
            }

        }

    }


    private static class IperfParser implements Function<String, Double> {

        @Override public Double apply(String s) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode;
            try {
                rootNode = objectMapper.readTree(s);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            final JsonNode end = rootNode.path("end");
            return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(end.iterator(), Spliterator.ORDERED),
                    false).filter(jsonNode -> jsonNode.has("bits_per_second")).mapToDouble(
                    jsonNode -> jsonNode.get("bits_per_second").doubleValue() * 0.125 * 1e-6)
                .average().orElseThrow(() -> new IllegalArgumentException("Unparsable output"));
        }
    }

    @Override protected void initialize(MonitorContext monitorContext,
        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);

        host = HostAndPort
            .fromHost(sensorConfiguration.getValue(HOST_FIELD).orElse(HOST_DEFAULT_VALUE));
        final Iperf3Installer iperf3Installer = new Iperf3Installer();
        if (!iperf3Installer.installed()) {
            try {
                iperf3Installer.install();
            } catch (IOException e) {
                throw new SensorInitializationException(e);
            }
        }
        try {
            new IperfServer().start();
        } catch (IOException e) {
            throw new SensorInitializationException(e);
        }
    }

    @Override protected Measurement<Double> measure() throws MeasurementNotAvailableException {
        return measurementBuilder(Double.class).now()
            .value(new IperfClient(host).measure(new IperfParser())).build();
    }
}
