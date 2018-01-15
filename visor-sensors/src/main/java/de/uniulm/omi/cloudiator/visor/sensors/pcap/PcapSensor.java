/*
 * Copyright (c) 2014-2017 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.sensors.pcap;

import com.google.common.base.MoreObjects;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.AbstractSensor;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by daniel on 10.03.17.
 */
public class PcapSensor extends AbstractSensor {

    private static final int SNAPLEN = 1600;
    private static final boolean PROMISCUOUS_MODE = false;
    private static final int READ_TIMEOUT = 0;

    private final static String PORT_FIELD = "port";
    private final static int PORT_DEFAULT_VALUE = 80;

    static Map<String, Long> numberOfIncomingPackages = new HashMap<>();
    static Map<String, Long> numberOfOutgoingPackages = new HashMap<>();

    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        PcapHandle.Builder phb = new PcapHandle.Builder("any").snaplen(SNAPLEN)
            .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
            .timeoutMillis(READ_TIMEOUT);

        PcapHandle handle = phb.build();

        handle.setFilter("port 80", BpfProgram.BpfCompileMode.OPTIMIZE);

        while (!Thread.currentThread().isInterrupted()) {
            Packet packet = handle.getNextPacket();
            if (packet == null) {
                continue;
            }
            final IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
            final TcpPacket tcpPacket = packet.get(TcpPacket.class);

            String srcAddr = ipV4Packet.getHeader().getSrcAddr().toString();
            String destAddr = ipV4Packet.getHeader().getDstAddr().toString();

            //check if incoming our outgoing
            if (tcpPacket.getHeader().getDstPort().valueAsInt() == 80) {
                //incoming
                numberOfIncomingPackages.merge(srcAddr, 1L, (a, b) -> a + b);
            } else if (tcpPacket.getHeader().getSrcPort().valueAsInt() == 80) {
                //outgoing
                numberOfOutgoingPackages.merge(destAddr, 1L, (a, b) -> a + b);
            } else {
                throw new IllegalStateException(
                    String.format("Could not classify packet %s as incoming or outgoing.", packet));
            }
            System.out.println(" ------ Incoming -------");
            System.out.println(numberOfIncomingPackages.toString());
            System.out.println(" ------ Outgoing -------");
            System.out.println(numberOfOutgoingPackages.toString());
        }
    }


    @Override protected void initialize(MonitorContext monitorContext,
        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);
    }

    @Override protected Measurement measureSingle() throws MeasurementNotAvailableException {
        return super.measureSingle();
    }

    @Override protected Set<Measurement> measureSet() throws MeasurementNotAvailableException {
        return super.measureSet();
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this).toString();
    }
}
