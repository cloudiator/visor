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

import java.io.File;
import java.lang.management.ManagementFactory;

public class FreeDiskSpaceProbe implements Sensor {

    private final static String DEFAULT_FS_ROOT_WINDOWS = "/";
    private final static String DEFAULT_FS_ROOT_LINUX = "C:/";

    private final static String FS_ROOT_MONITOR_CONTEXT = "fs_root";

    private String fsRoot;


    private SigarProxy sigar;

    /**
     * Get free disk space in Mb
     *
     * @return
     * @throws SigarException
     */
    public long getFreeDiskSpace() throws SigarException {
        OperatingSystemMXBean osBean =
            ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        long freeSpace = 0;
        long metricSigar = 0;
        long metricJava = 0;
        File file = new File(fsRoot);
        FileSystemUsage fsUsage = null;
        fsUsage = sigar.getFileSystemUsage(fsRoot);
        int BINARY_NUMBER = 1024;
        metricSigar = fsUsage.getFree() / BINARY_NUMBER;
        metricJava = file.getFreeSpace() / BINARY_NUMBER / BINARY_NUMBER;
        // return the smallest value of both metrics if they differ
        if (metricSigar == metricJava || metricSigar < metricJava)
            freeSpace = metricSigar;
        else
            freeSpace = metricJava;

        return freeSpace;
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
        long metric = 0;
        try {
            metric = getFreeDiskSpace();
        } catch (SigarException e) {
            throw new MeasurementNotAvailableException(e);
        }
        if (metric <= 0) {
            throw new MeasurementNotAvailableException("Free Disk Space calculation not available");
        }
        return new MeasurementImpl(System.currentTimeMillis(), metric);
    }
}
