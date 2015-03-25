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

import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;


public class FreeDiskSpaceProbe {
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
		metricSigar = fsUsage.getFree() / 1024;
		metricJava = file.getFreeSpace() / 1024 / 1024;
		// return the smallest value of both metrics if they differ
		if(metricSigar == metricJava || metricSigar < metricJava)
			freeSpace = metricSigar;
		else 
			freeSpace = metricJava;
		
		return freeSpace;
	}
}
