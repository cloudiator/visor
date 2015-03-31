/*
 *
 *  * Copyright (c) 2015 University of Stuttgart
 *  *
 *  * See the NOTICE file distributed with this work for additional information
 *  * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */
package de.uniulm.omi.executionware.agent.monitoring.sensors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.hyperic.sigar.NfsClientV2;
import org.hyperic.sigar.NfsClientV3;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

import com.google.common.base.Optional;

import de.uniulm.omi.executionware.agent.monitoring.api.InvalidMonitorContextException;
import de.uniulm.omi.executionware.agent.monitoring.api.Measurement;
import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.api.Sensor;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;

public class NFSStatusProbe implements Sensor{
	private static String FILE_NAME_TEST="/file_that_do_not_exist.txt";
	Sigar sigarImpl;
	SigarProxy sigar;
	public NFSStatusProbe(){
		this.sigarImpl = new Sigar();
	}
	/**
	 * Check if nfs is available, e.g., mounted on the system.
	 * Make sure that the method is allowed to write to nfs folder.
	 * It is writing and deleting the file, measures the number of create operations to nfs.
	 * If the number is increased, NFS is available, if not, it is offline.
	 * Only shares accessed with NFSv3 protocol are supported by this method
	 * @param nfsMountPoint
	 * @return
	 * @throws SigarException
	 * @throws IOException
	 */
	public boolean isNFSV3Available(String nfsMountPoint) throws SigarException, IOException{
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		long numberOfCreates = 0;
		try{
			NfsClientV3 nfsClient = sigar.getNfsClientV3();
			nfsClient = sigar.getNfsClientV3();
			numberOfCreates = nfsClient.getCreate();
			// file name should not conflict with any of already existing files in nfs directory
			File file = new File(nfsMountPoint+FILE_NAME_TEST);
			file.createNewFile();
			file.delete();
			this.sigar = SigarProxyCache.newInstance(sigarImpl);
			nfsClient = sigar.getNfsClientV3();
			if(nfsClient.getCreate() > numberOfCreates)
				return true;
			else
				return false;	
		}
		// sigar throws an exception if nfs was not mounted
		catch(SigarException e){
			return false;
		}
		
	}
	/**
	 * Check if nfs is available, e.g., mounted on the system.
	 * Make sure that the method is allowed to write to nfs folder.
	 * It is writing and deleting the file, measures the number of create operations to nfs.
	 * If the number is increased, NFS is available, if not, it is offline.
	 * Only shares accessed with NFSv2 protocol are supported by this method
	 * @param nfsMountPoint
	 * @return
	 * @throws SigarException
	 * @throws IOException
	 */
	public boolean isNFSV2Available(String nfsMountPoint) throws SigarException, IOException{
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		long numberOfCreates = 0;
		try{
			NfsClientV2 nfsClient = sigar.getNfsClientV2();
			nfsClient = sigar.getNfsClientV2();
			numberOfCreates = nfsClient.getCreate();
			// file name should not conflict with any of already existing files in nfs directory
			File file = new File(nfsMountPoint+ FILE_NAME_TEST);
			file.createNewFile();
			file.delete();
			this.sigar = SigarProxyCache.newInstance(sigarImpl);
			nfsClient = sigar.getNfsClientV2();
			if(nfsClient.getCreate() > numberOfCreates)
				return true;
			else
				return false;	
		}
		// sigar throws an exception if nfs was not mounted
		catch(SigarException e){
			return false;
		}
	}
	@Override
	public void init() throws SensorInitializationException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setMonitorContext(Optional<MonitorContext> monitorContext)
			throws InvalidMonitorContextException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Measurement getMeasurement() throws MeasurementNotAvailableException {		
		Properties properties;
		FileInputStream in;
		boolean isNFS = false;
		try {
			properties = new Properties();
			in = new FileInputStream("config.properties");
			properties.load(in);
			isNFS = isNFSV3Available(properties.getProperty("NFS_MOUNT_POINT"));
		} catch (IOException | SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!isNFS) {
	        throw new MeasurementNotAvailableException("NFS is not available");
	    }
		return new MeasurementImpl(System.currentTimeMillis(), isNFS);
	}
}
	