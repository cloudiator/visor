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

package de.uniulm.omi.cloudiator.visor.sensors.sigar;

import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.SigarException;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 27.01.16.
 */
public class SigarNetworkSensor extends AbstractSigarSensor {

    private enum Type implements MeasurableType {
        RX {
            @Override public long getMeasurement(NetInterfaceStat netInterfaceStat) {
                return netInterfaceStat.getRxBytes();
            }
        },
        TX {
            @Override public long getMeasurement(NetInterfaceStat netInterfaceStat) {
                return netInterfaceStat.getTxBytes();
            }
        },
        RXTX {
            @Override public long getMeasurement(NetInterfaceStat netInterfaceStat) {
                return netInterfaceStat.getRxBytes() + netInterfaceStat.getTxBytes();
            }
        }
    }


    private enum Size implements FromBytes {
        BYTE {
            @Override public double fromBytes(double bytes) {
                return bytes;
            }
        },
        KB {
            @Override public double fromBytes(double bytes) {
                return bytes / 1000;
            }
        },
        MB {
            @Override public double fromBytes(double bytes) {
                return bytes / 1000 / 1000;
            }
        },
        GB {
            @Override public double fromBytes(double bytes) {
                return bytes / 1000 / 1000 / 1000;
            }
        }
    }


    private interface MeasurableType {
        long getMeasurement(NetInterfaceStat netInterfaceStat);
    }


    private interface FromBytes {
        double fromBytes(double bytes);
    }


    private static final String NETWORK_INTERFACE_CONFIG = "sigar.network.interface";
    private static final String NETWORK_TYPE_CONFIG = "sigar.network.type";
    private static final String NETWORK_SIZE_CONFIG = "sigar.network.size";
    private static final String NETWORK_TIMEUNIT_CONFIG = "sigar.network.time";

    private static final String DEFAULT_TIMEUNIT = "SECONDS";
    private static final String DEFAULT_SIZE = "MB";

    private Optional<String> netInterface;
    private Type type;
    private TimeUnit timeUnit;
    private Size size;

    private Measurement lastMeasurement;

    @Override protected void initialize(MonitorContext monitorContext,
        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);
        netInterface = sensorConfiguration.getValue(NETWORK_INTERFACE_CONFIG);
        try {
            type = Type.valueOf(sensorConfiguration.getValue(NETWORK_TYPE_CONFIG).orElseThrow(
                () -> new SensorInitializationException(String
                    .format("Required configuration parameter %s was not set",
                        NETWORK_TYPE_CONFIG))));
        } catch (IllegalArgumentException e) {
            throw new SensorInitializationException(String.format(
                "Invalid value supplied for configuration parameter %s. %s was given allowed values are: %s",
                NETWORK_TYPE_CONFIG, sensorConfiguration.getValue(NETWORK_TYPE_CONFIG),
                Arrays.toString(Type.values())), e);
        }

        try {
            timeUnit = TimeUnit.valueOf(
                sensorConfiguration.getValue(NETWORK_TIMEUNIT_CONFIG).orElse(DEFAULT_TIMEUNIT));
        } catch (IllegalArgumentException e) {
            throw new SensorInitializationException(String.format(
                "Invalid value supplied for configuration parameter %s. %s vas given allowed values are %s",
                NETWORK_TIMEUNIT_CONFIG, sensorConfiguration.getValue(NETWORK_TIMEUNIT_CONFIG),
                Arrays.toString(TimeUnit.values())), e);
        }

        try {
            size = Size.valueOf(
                sensorConfiguration.getValue(NETWORK_SIZE_CONFIG).orElse(DEFAULT_SIZE));
        } catch (IllegalArgumentException e) {
            throw new SensorInitializationException(String.format(
                "Invalid value supplied for configuration parameter %s. %s vas given allowed values are %s",
                NETWORK_SIZE_CONFIG, sensorConfiguration.getValue(NETWORK_SIZE_CONFIG),
                Arrays.toString(Size.values())), e);
        }

    }

    private long getCurrentBytes() throws SigarException {
        String interfaceName;
        if (netInterface.isPresent()) {
            interfaceName = netInterface.get();
        } else {
            interfaceName = sigar().getNetInterfaceConfig().getDescription();
        }

        return type.getMeasurement(sigar().getNetInterfaceStat(interfaceName));
    }

    @Override protected Measurement measure() throws MeasurementNotAvailableException {
        try {
            Measurement currentMeasurement =
                measurementBuilder().now().value(getCurrentBytes()).build();
            if (lastMeasurement == null) {
                lastMeasurement = currentMeasurement;
                throw new MeasurementNotAvailableException("no last measurement data available");
            }
            long differenceBytes =
                (long) currentMeasurement.getValue() - (long) lastMeasurement.getValue();
            long differenceTime = timeUnit
                .convert(currentMeasurement.getTimestamp() - lastMeasurement.getTimestamp(),
                    TimeUnit.MILLISECONDS);
            double dataPerTimeUnit = size.fromBytes(differenceBytes) / differenceTime;

            lastMeasurement = currentMeasurement;
            return measurementBuilder().now().value(dataPerTimeUnit).build();

        } catch (SigarException e) {
            throw new MeasurementNotAvailableException(e);
        }
    }
}
