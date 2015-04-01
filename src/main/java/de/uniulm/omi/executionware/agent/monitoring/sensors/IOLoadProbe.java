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


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.hyperic.sigar.DiskUsage;
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
/**
 * Instance of this class will measure the IO load in the system. It is run in a separate thread.
 * IO overhead is presented by read/write requests per second.
 * Do measurements in two subthreads
 */
public class IOLoadProbe implements Sensor{

	Sigar sigarImpl;
	SigarProxy sigar;
	String fsRoot;
	
	public IOLoadProbe(String fsRoot){
		this.sigarImpl = new Sigar();
		this.sigar=SigarProxyCache.newInstance(sigarImpl);
		this.fsRoot = fsRoot;
		
	}
	
	public String outputDisk(String name) throws SigarException {
		 DiskUsage disk = sigar.getDiskUsage(name);
		 return "Reads-bytes: " + Sigar.formatSize(disk.getReadBytes()) + " | Writes-bytes: " + Sigar.formatSize(disk.getWriteBytes()) 
				 + " | Reads: " + disk.getReads() + " | Writes: " + disk.getWrites();
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
		String diskIO="";
		try {
			Properties properties = new Properties();
			FileInputStream in = new FileInputStream("config.properties");
			properties.load(in);
			diskIO = outputDisk(properties.getProperty("FS_ROOT"));
		} catch (IOException | SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//if (diskIO.equals("")) {
	      //  throw new MeasurementNotAvailableException("IODisk calculation wasn´t possible!");
	    //}
		return new MeasurementImpl(System.currentTimeMillis(), diskIO);
		
	}
}

