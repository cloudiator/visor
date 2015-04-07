/*
 * Copyright (c) 2015 University of Stuttgart
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

/**
 * This file was modified by University of Ulm.
 */

package de.ustutt.cloudiator.visor.monitoring.sensors;

import de.uniulm.omi.cloudiator.visor.monitoring.api.*;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MonitorContext;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;



/**
 * Return latency when connecting to the given host in milliseconds
 */
public class AverageLatencyProbe implements Sensor {

    private final static int DEFAULT_PING_LOOP = 5;
    private final static int DEFAULT_PING_PORT = 80;

    private final static String PING_LOOP_MONITOR_CONTEXT = "ping_loop";
    private final static String PING_PORT_MONITOR_CONTEXT = "ping_port";
    private final static String PING_IP_MONITOR_CONTEXT = "ping_ip";

    private int pingLoop;
    private int pingPort;
    private String pingIp;

    public double getAverageLatency() throws IOException {
        long latency = 0;
        long startTime;
        long endTime;
        InetAddress address = InetAddress.getByName(pingIp);
        SocketAddress socketAddress = new InetSocketAddress(address, pingPort);
        // calculate average for several latency values
        for (int i = 0; i < pingLoop; i++) {
            Socket s = new Socket();
            startTime = System.currentTimeMillis();
            s.connect(socketAddress);
            endTime = System.currentTimeMillis();
            latency += endTime - startTime;
            s.close();
        }
        return latency / pingLoop;
    }

    @Override public void init() throws SensorInitializationException {
        // TODO Auto-generated method stub
    }

    @Override public void setMonitorContext(MonitorContext monitorContext)
        throws InvalidMonitorContextException {

        try {
            this.pingLoop = Integer.parseInt(monitorContext
                .getOrDefault(PING_LOOP_MONITOR_CONTEXT, String.valueOf(DEFAULT_PING_LOOP)));
            this.pingPort = Integer.parseInt(monitorContext
                .getOrDefault(PING_PORT_MONITOR_CONTEXT, String.valueOf(DEFAULT_PING_PORT)));
        } catch (Exception e) {
            throw new InvalidMonitorContextException(e);
        }

        if (!monitorContext.hasValue(PING_IP_MONITOR_CONTEXT)) {
            throw new InvalidMonitorContextException(String
                .format("The monitor context %s is mandatory for this probe.",
                    PING_IP_MONITOR_CONTEXT));
        }
        this.pingIp = monitorContext.getValue(PING_IP_MONITOR_CONTEXT);
    }

    @Override public Measurement getMeasurement() throws MeasurementNotAvailableException {
        double val = 0;
        try {
            val = getAverageLatency();
        } catch (IOException e) {
            throw new MeasurementNotAvailableException(e);
        }
        if (val <= 0) {
            throw new MeasurementNotAvailableException("Latency Calculation isn´t available");
        }
        return new MeasurementImpl(System.currentTimeMillis(), val);
    }
}
