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

import java.io.File;
import java.io.IOException;

import de.uniulm.omi.cloudiator.visor.monitoring.api.*;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MonitorContext;
import org.hyperic.sigar.NfsClientV2;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

public class NFSV2Probe implements Sensor {
	Sigar sigarImpl;
	SigarProxy sigar;
	
	public NFSV2Probe(){
		this.sigarImpl = new Sigar();
	}
	
	/**
	 * Check if nfs is available, e.g., mounted on the system.
	 * Make sure that the method is allowed to write to nfs folder.
	 * It is writing and deleting the file, measures the number of create operations to nfs.
	 * If the number is increased, NFS is available, if not, it is offline.
	 * Only shares accessed with NFSv2 protocol are supported by this method
	 * @return
	 * @throws SigarException
	 * @throws IOException
	 */
	public boolean isNFSV2Available() throws SigarException, IOException{
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		long numberOfCreates = 0;
		try{
			NfsClientV2 nfsClient = sigar.getNfsClientV2();
			nfsClient = sigar.getNfsClientV2();
			numberOfCreates = nfsClient.getCreate();
			// file name should not conflict with any of already existing files in nfs directory

			// TODO: fix, use monitor context as intended. Temp use new file to avoid compiler exception.
				//TODO: do you really need to write a file to check if the mount point is available?
			//File file = new File(MonitorContext.NFS_MOUNT_POINT+ MonitorContext.FILE_NAME_TEST);
			File file = new File("");
			file.createNewFile();
			file.delete();
			this.sigar = SigarProxyCache.newInstance(sigarImpl);
			nfsClient = sigar.getNfsClientV2();
			if(nfsClient.getCreate() > numberOfCreates)
				return true;
			else
				return false;	
		}		
		catch(SigarException e){
			return false;
		}
	}
	
	@Override
	public void init() throws SensorInitializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMonitorContext(MonitorContext monitorContext)
			throws InvalidMonitorContextException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Measurement getMeasurement() throws MeasurementNotAvailableException {
		boolean isNFS2 = false;
		try {
			isNFS2 = isNFSV2Available();
		} catch (IOException | SigarException e) {
			// TODO Auto-generated catch block

			//TODO: we do not want stack traces. Rethrow it as MeasurementNotAvailableException. This is what this is for.
			e.printStackTrace();
		}
		if (!isNFS2) {
	        throw new MeasurementNotAvailableException("NFS V2 is not available");
	    }

		//TODO: fix, isNFS32 is always true, however the metric should represent also the state when it is not available?
		return new MeasurementImpl(System.currentTimeMillis(), isNFS2);
	}

}
