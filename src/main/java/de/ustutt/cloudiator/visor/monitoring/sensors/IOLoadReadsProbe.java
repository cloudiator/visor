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

import com.sun.management.OperatingSystemMXBean;
import de.uniulm.omi.cloudiator.visor.monitoring.api.*;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MonitorContext;
import org.hyperic.sigar.*;

import java.lang.management.ManagementFactory;


public class IOLoadReadsProbe implements Sensor {

    private final static String DEFAULT_FS_ROOT_WINDOWS = "/";
    private final static String DEFAULT_FS_ROOT_LINUX = "C:/";

    private final static String FS_ROOT_MONITOR_CONTEXT = "fs_root";

    private String fsRoot;

    private SigarProxy sigar;

    public long outputDisk() throws SigarException {
        DiskUsage disk = sigar.getDiskUsage(fsRoot);
        return disk.getReads();
    }

    @Override public void init() throws SensorInitializationException {
        Sigar sigarImpl = new Sigar();
        this.sigar = SigarProxyCache.newInstance(sigarImpl);
    }

    @Override public void setMonitorContext(MonitorContext monitorContext)
        throws InvalidMonitorContextException {
        if (monitorContext.hasValue(FS_ROOT_MONITOR_CONTEXT)) {
            this.fsRoot = monitorContext.getValue(FS_ROOT_MONITOR_CONTEXT);
        } else {
            OperatingSystemMXBean osBean =
                ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            fsRoot =
                osBean.getName().contains("win") ? DEFAULT_FS_ROOT_WINDOWS : DEFAULT_FS_ROOT_LINUX;
        }
    }

    @Override public Measurement getMeasurement() throws MeasurementNotAvailableException {
        try {
            long diskIO = outputDisk();
            return new MeasurementImpl(System.currentTimeMillis(), diskIO);
        } catch (SigarException e) {
            throw new MeasurementNotAvailableException(e);
        }
    }

}
