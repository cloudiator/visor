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
import java.io.IOException;

import org.hyperic.sigar.NfsClientV2;
import org.hyperic.sigar.NfsClientV3;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

public class NFSStatusProbe {
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
			File file = new File(nfsMountPoint+"/file_that_do_not_exist.txt");
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
			File file = new File(nfsMountPoint+"/file_that_do_not_exist.txt");
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
}
	
