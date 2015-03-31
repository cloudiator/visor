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

import org.hyperic.sigar.FileSystemUsage;
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


public class FreeDiskSpaceProbe implements Sensor{

	private static int BINARY_NUMBER= 1024;
	Sigar sigarImpl;
	SigarProxy sigar;
	public FreeDiskSpaceProbe (){
		this.sigarImpl = new Sigar();
		this.sigar=SigarProxyCache.newInstance(sigarImpl);
	}
	/**
	 * Get free disk space in Mb
	 * @param fsRoot
	 * @return
	 * @throws SigarException
	 */
	public long getFreeDiskSpace(String fsRoot) throws SigarException
	{
		long freeSpace = 0;
		long metricSigar = 0;
		long metricJava = 0;
		File file = new File(fsRoot);
		FileSystemUsage fsUsage = null;
		fsUsage = sigar.getFileSystemUsage(fsRoot);
		metricSigar = fsUsage.getFree() / BINARY_NUMBER;
		metricJava = file.getFreeSpace() / BINARY_NUMBER / BINARY_NUMBER;
		// return the smallest value of both metrics if they differ
		if(metricSigar == metricJava || metricSigar < metricJava)
			freeSpace = metricSigar;
		else 
			freeSpace = metricJava;
		
		return freeSpace;
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
		long metric= 0;
		 try {
			Properties properties = new Properties();
			FileInputStream in = new FileInputStream("config.properties");
			properties.load(in);
			metric = getFreeDiskSpace(properties.getProperty("FS_ROOT"));
		} catch (IOException | SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (metric <= 0) {
		        throw new MeasurementNotAvailableException("Free Disk Space calculation not available");
		}
		return new MeasurementImpl(System.currentTimeMillis(), metric);
	}
}
