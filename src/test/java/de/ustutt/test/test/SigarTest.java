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
package de.ustutt.test.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Iostat;
import org.junit.Ignore;
import org.junit.Test;

import de.uniulm.omi.executionware.agent.monitoring.sensors.AverageLatencyProbe;
import de.uniulm.omi.executionware.agent.monitoring.sensors.FreeDiskSpaceProbe;
import de.uniulm.omi.executionware.agent.monitoring.sensors.IOLoadProbe;
import de.uniulm.omi.executionware.agent.monitoring.sensors.NFSStatusProbe;
import de.uniulm.omi.executionware.agent.monitoring.sensors.NetworkStateProbe;

public class SigarTest {
	Properties properties;
	public SigarTest() throws IOException{
		properties = new Properties();
		String propFileName = "config.properties";
		FileInputStream in = new FileInputStream(propFileName);
		properties.load(in);
	}

	@Ignore
	@Test
	public void testFreeSpace() throws SigarException
	{
		FreeDiskSpaceProbe diskprobe = new FreeDiskSpaceProbe();
		System.out.println("Free disk space is: "+diskprobe.getFreeDiskSpace(properties.getProperty("FS_ROOT")));
	}
	//@Ignore
	@Test
	public void testNetworkState() throws IOException, SigarException, InterruptedException{
		double averageRxRate;
		double averageTxRate;
		NetworkStateProbe networkState = new NetworkStateProbe();
		averageRxRate = networkState.getAverageRxRate();
		averageTxRate = networkState.getAverageTxRate();
		System.out.println("Average receive rate is "+averageRxRate/1024 + " kBytes/sec");
		System.out.println("Average transmit rate is "+averageTxRate/1024 + " kBytes/sec");
	}
	@Ignore
	@Test
	public void testAverageNumberOfReads() throws SigarException{
		IOLoadProbe ioMonitor = new IOLoadProbe(properties.getProperty("FS_ROOT"));
		System.out.println(ioMonitor.outputDisk(properties.getProperty("FS_ROOT")));
	}
	@Ignore
	@Test
	public void testAverageLatency() throws IOException{
		String ip = properties.getProperty("PING_IP");
		int port = Integer.parseInt(properties.getProperty("PING_PORT"));
		int loopPeriod = Integer.parseInt(properties.getProperty("PING_LOOP")); 
		AverageLatencyProbe latency = new AverageLatencyProbe();
		System.out.println("Latency to "+ip+": "+ latency.getAverageLatence(ip, port, loopPeriod) + "ms");
	}
	@Ignore
	@Test
	public void testUsedBandwidth() throws SigarException, InterruptedException{
		NetworkStateProbe networkState = new NetworkStateProbe();
		System.out.println("Average used download bandwidth is "+networkState.getAverageUsedDownloadBandwidth(100) +"%");
		System.out.println("Average used upload bandwidth is "+networkState.getAverageUsedUploadBandwidth(100) +"%");
	}
	@Ignore
	@Test
	public void testNFSStatus() throws SigarException, IOException {
		NFSStatusProbe nfsStatus = new NFSStatusProbe();
		System.out.println("NFS availability: " + nfsStatus.isNFSV3Available(properties.getProperty("NFS_MOUNT_POINT")));
	}
}
	
	
