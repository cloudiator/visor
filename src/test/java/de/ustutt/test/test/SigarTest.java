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
import java.io.IOException;
import org.hyperic.sigar.SigarException;
import org.junit.Test;

import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;
import de.ustutt.cloudiator.visor.monitoring.sensors.AverageLatencyProbe;
import de.ustutt.cloudiator.visor.monitoring.sensors.AvgReceivedBytesProbe;
import de.ustutt.cloudiator.visor.monitoring.sensors.AvgTransmittedBytesProbe;
import de.ustutt.cloudiator.visor.monitoring.sensors.FreeDiskSpaceProbe;
import de.ustutt.cloudiator.visor.monitoring.sensors.IOLoadReadsProbe;
import de.ustutt.cloudiator.visor.monitoring.sensors.IOLoadWritesProbe;
import de.ustutt.cloudiator.visor.monitoring.sensors.NFSV2Probe;
import de.ustutt.cloudiator.visor.monitoring.sensors.NFSV3Probe;
import de.ustutt.cloudiator.visor.monitoring.sensors.BandwithDownloadProbe;
import de.ustutt.cloudiator.visor.monitoring.sensors.BandwithUploadProbe;

public class SigarTest {
	
	public SigarTest() throws IOException{
	}

	//@Ignore
	@Test
	public void testFreeSpace() throws SigarException
	{
		FreeDiskSpaceProbe diskprobe = new FreeDiskSpaceProbe();
		System.out.println("Free disk space is: "+diskprobe.getFreeDiskSpace());
	}
	
	//@Ignore
	@Test
	public void testAvgTransmittedBytes() throws IOException, SigarException, InterruptedException{
		double averageTxRate;
		AvgTransmittedBytesProbe networkState = new AvgTransmittedBytesProbe();
		averageTxRate = networkState.getAverageTxRate();
		System.out.println("Average transmit rate is "+averageTxRate/1024 + " kBytes/sec");
	}
	
	//@Ignore
	@Test
	public void testAvgReceivedBytes() throws IOException, SigarException, InterruptedException{
		double averageRxRate;
		AvgReceivedBytesProbe networkState = new AvgReceivedBytesProbe();
		averageRxRate = networkState.getAverageRxRate();
		System.out.println("Average received rate is "+averageRxRate/1024 + " kBytes/sec");
	}
	
	
	//@Ignore
	@Test
	public void testAverageNumberOfReads() throws SigarException{
		IOLoadReadsProbe ioMonitor = new IOLoadReadsProbe();
		System.out.println(ioMonitor.outputDisk());
	}
	//@Ignore
	@Test
	public void testAverageNumberOfWrites() throws SigarException{
		IOLoadWritesProbe ioMonitor = new IOLoadWritesProbe();
		System.out.println(ioMonitor.outputDisk());
	}
	//@Ignore
	@Test
	public void testAverageLatency() throws IOException{
		AverageLatencyProbe latency = new AverageLatencyProbe();
		System.out.println("Latency to "+MonitorContext.PING_IP+": "+ latency.getAverageLatence() + "ms");
	}
	
	//@Ignore
	@Test
	public void testUsedUploadBandwidth() throws SigarException, InterruptedException{
		BandwithUploadProbe networkStateUpload = new BandwithUploadProbe();
		System.out.println("Average used upload bandwidth is "+networkStateUpload.getAverageUsedUploadBandwidth() +"%");
	}
	
	//@Ignore
	@Test
	public void testUsedDownloadBandwidth() throws SigarException, InterruptedException{
		BandwithDownloadProbe networkStateDownload = new BandwithDownloadProbe();
		System.out.println("Average used download bandwidth is "+networkStateDownload.getAverageUsedDownloadBandwidth() +"%");
	}
		
	//@Ignore
	@Test
	public void testNFSStatus2() throws SigarException, IOException {
		NFSV2Probe nfs2 = new NFSV2Probe();
		System.out.println("NFS V2 availability: " + nfs2.isNFSV2Available());
	}
	//@Ignore
	@Test
	public void testNFSStatus3() throws SigarException, IOException {
		NFSV3Probe nfs3 = new NFSV3Probe();
		System.out.println("NFS V3 availability: " + nfs3.isNFSV3Available());
	}
	
}
	
	
