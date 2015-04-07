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

import java.io.File;
import java.io.IOException;


public class NFSV3Probe implements Sensor {
    Sigar sigarImpl;
    SigarProxy sigar;

    public NFSV3Probe() {
        this.sigarImpl = new Sigar();
    }

    /**
     * Check if nfs is available, e.g., mounted on the system.
     * Make sure that the method is allowed to write to nfs folder.
     * It is writing and deleting the file, measures the number of create operations to nfs.
     * If the number is increased, NFS is available, if not, it is offline.
     * Only shares accessed with NFSv3 protocol are supported by this method
     *
     * @return
     * @throws SigarException
     * @throws IOException
     */
    public boolean isNFSV3Available() throws SigarException, IOException {
        this.sigar = SigarProxyCache.newInstance(sigarImpl);
        long numberOfCreates = 0;
        try {
            NfsClientV3 nfsClient = sigar.getNfsClientV3();
            nfsClient = sigar.getNfsClientV3();
            numberOfCreates = nfsClient.getCreate();
            // file name should not conflict with any of already existing files in nfs directory
            //TODO: again, do not abuse the monitor context. See other sensor for how to do it.
            //File file = new File(MonitorContext.NFS_MOUNT_POINT+MonitorContext.FILE_NAME_TEST);
            File file = new File("");
            file.createNewFile();
            file.delete();
            this.sigar = SigarProxyCache.newInstance(sigarImpl);
            nfsClient = sigar.getNfsClientV3();
            if (nfsClient.getCreate() > numberOfCreates)
                return true;
            else
                return false;
        }
        // sigar throws an exception if nfs was not mounted
        catch (SigarException e) {
            return false;
        }

    }

    @Override public void init() throws SensorInitializationException {
        // TODO Auto-generated method stub

    }

    @Override public void setMonitorContext(MonitorContext monitorContext)
        throws InvalidMonitorContextException {
        // TODO Auto-generated method stub

    }

    @Override public Measurement getMeasurement() throws MeasurementNotAvailableException {
        boolean isNFS3 = false;
        try {
            isNFS3 = isNFSV3Available();
        } catch (IOException | SigarException e) {
            // TODO Auto-generated catch block
            //TODO: fix, no stacktraces! Rethrow as MeasurementNotAvailableException
            e.printStackTrace();
        }
        if (!isNFS3) {
            throw new MeasurementNotAvailableException("NFS V3 is not available");
        }
        //TODO: useless as well, as it will always report true....
        return new MeasurementImpl(System.currentTimeMillis(), isNFS3);
    }

}
