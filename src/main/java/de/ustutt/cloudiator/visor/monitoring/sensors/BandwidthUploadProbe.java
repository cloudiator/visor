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
import org.hyperic.sigar.*;

import java.util.ArrayList;

public class BandwidthUploadProbe implements Sensor {

    private static final int SMALL_CYCLE = 1000;
    private static final int BIG_CYCLE = 5000;
    Sigar sigarImpl;
    SigarProxy sigar;

    public BandwidthUploadProbe() {
        this.sigarImpl = new Sigar();

    }

    /**
     * Provide channel width in Mbit/sec, retrieve used bandwidth in percentage from this value.
     * Transmitter bandwidth is considered here
     *
     * @return
     * @throws InterruptedException
     * @throws SigarException
     */
    public double getAverageUsedUploadBandwidth() throws SigarException, InterruptedException {
        double percentage = 100.0;
        // make both values of the same scope, KBytes/s
        int eight = 8;
        int binaryNumber = 1024;
        double channelWidthInKBytesPerSecond =
            ((MonitorContext.CHANNEL_WIDTH / eight) * binaryNumber);
        double rxRateInKBytesPerSecond = this.getAverageTxRate() / binaryNumber;
        percentage = (rxRateInKBytesPerSecond * percentage) / channelWidthInKBytesPerSecond;

        // round to 3 symbols after the dot
        int THOUSAND = 1000;
        int roundedValue = (int) (percentage * THOUSAND);
        percentage = (double) roundedValue / THOUSAND;
        return percentage;
    }

    /**
     * Average rate of transmitted bytes in bytes per second. Blocking method, execute it in a separate thread
     *
     * @return
     * @throws SigarException
     * @throws InterruptedException
     */
    public double getAverageTxRate() throws SigarException, InterruptedException {
        int smallCycle = SMALL_CYCLE;
        int bigCycle = BIG_CYCLE;
        NetInterfaceStat netStat = null;
        long txBytesLastCycle = 0;
        long txBytesNewCycle = 0;
        ArrayList<Long> txBytesTotal = new ArrayList<Long>();
        long averageBytesPerSecond;

        // measure transmitted bytes during bigCycle period
        for (int i = 0; i <= bigCycle; i += smallCycle) {
            txBytesNewCycle = 0;
            this.sigar = SigarProxyCache.newInstance(sigarImpl);

            // measure the number of received bytes on all interfaces
            for (String ni : sigar.getNetInterfaceList()) {
                netStat = this.sigar.getNetInterfaceStat(ni);
                if (i == 0)
                    txBytesLastCycle += netStat.getTxBytes();
                else
                    txBytesNewCycle += netStat.getTxBytes();
            }
            // we are interested on the changed values
            if (txBytesNewCycle - txBytesLastCycle > 0) {
                txBytesTotal.add(txBytesNewCycle - txBytesLastCycle);
                txBytesLastCycle = txBytesNewCycle;
            }

            Thread.sleep(smallCycle);
        }
        // get average value for all non zero measurements
        averageBytesPerSecond = this.calculateAverageRate(txBytesTotal);

        return averageBytesPerSecond;
    }

    //TODO: substitute by some more beautiful calculation
    private long calculateAverageRate(ArrayList<Long> measurements) {
        long result = 0;
        for (Long m : measurements)
            result += m;
        if (result != 0)
            result = result / measurements.size();
        return result;
    }



    @Override public void init() throws SensorInitializationException {
        // TODO Auto-generated method stub

    }

    @Override public void setMonitorContext(MonitorContext monitorContext)
        throws InvalidMonitorContextException {
        // TODO Auto-generated method stub

    }

    @Override public Measurement getMeasurement() throws MeasurementNotAvailableException {
        //in %
        double averageTxRate = 0;
        try {
            averageTxRate = getAverageUsedUploadBandwidth();
        } catch (InterruptedException | SigarException e) {
            throw new MeasurementNotAvailableException(e);
        }
        if (averageTxRate <= 0) {
            throw new MeasurementNotAvailableException(
                "Network metric Upload rate isn´t available");
        }

        return new MeasurementImpl(System.currentTimeMillis(), averageTxRate);
    }

}
