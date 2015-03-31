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
	//protected static Queue queue;
	public SigarTest() throws IOException{
		properties = new Properties();
		String propFileName = "config.properties";
		FileInputStream in = new FileInputStream(propFileName);
		properties.load(in);
		//queue = new Queue(properties.getProperty("io_test_directory")+"foo.txt");
	}

	//@Ignore
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
	//@Ignore
	@Test
	public void testAverageNumberOfReads() throws SigarException, InterruptedException
	{
		IOLoadProbe ioMonitor = new IOLoadProbe(properties.getProperty("FS_ROOT"),Integer.valueOf(properties.getProperty("big_cycle")));
		//MyReader reader = new MyReader(queue);
		//MyWriter writer = new MyWriter(queue);
		//ioMonitor.start();
		//reader.start();
		//writer.start();
		// measure read write requests during 2 seconds
		//Thread.sleep(2000);
		System.out.println(ioMonitor.outputDisk(properties.getProperty("FS_ROOT")));
		
		//ss.outputFileSystem("2 "+ properties.getProperty("FS_ROOT"));
	}
	//@Ignore
	@Test
	public void testAverageLatency() throws IOException{
		String ip = properties.getProperty("PING_IP");
		int port = Integer.parseInt(properties.getProperty("PING_PORT"));
		int loopPeriod = Integer.parseInt(properties.getProperty("PING_LOOP")); 
		AverageLatencyProbe latency = new AverageLatencyProbe();
		System.out.println("Latency to "+ip+": "+ latency.getAverageLatence(ip, port, loopPeriod) + "ms");
	}
	//@Ignore
	@Test
	public void testUsedBandwidth() throws SigarException, InterruptedException{
		NetworkStateProbe networkState = new NetworkStateProbe();
		System.out.println("Average used download bandwidth is "+networkState.getAverageUsedDownloadBandwidth(100) +"%");
		System.out.println("Average used upload bandwidth is "+networkState.getAverageUsedUploadBandwidth(100) +"%");
	}
	//@Ignore
	@Test
	public void testNFSStatus() throws SigarException, IOException {
		NFSStatusProbe nfsStatus = new NFSStatusProbe();
		System.out.println("NFS availability: " + nfsStatus.isNFSV3Available(properties.getProperty("NFS_MOUNT_POINT")));
	}
	/**
	 * Object of this class is needed for synchronization of reader/writer threads
	 *
	 */
/*	class Queue{
		private int size = 0; 
		String fileName;
		public  Queue(String fileName){
			this.fileName = fileName;
		}
		public synchronized void write() throws InterruptedException{
			while(this.size > 0){
				wait();
			}
			// write file on disk
			this.writeOnDisk(fileName);
			size++;
			notifyAll();
		}
		public synchronized void read() throws InterruptedException, IOException{
			while(this.size==0){
				wait();
			}
			// read file from disk and delete
			this.readFromDiskAndDelete(fileName);
			size--;
			notifyAll();
		}
		public String getFilePath(){
			return this.fileName;
		}
		private void writeOnDisk(String filePath){
			File file = new File(filePath);
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(filePath,"UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			writer.println("some random string");
			writer.close();
			System.out.println("File was written");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		private void readFromDiskAndDelete(String filePath) throws IOException{
			int bytes = 0;
			File file = new File(filePath);
			BufferedReader reader = null;
			try {
				reader =new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				
				bytes = reader.read();
				System.out.println(reader.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Number of bytes read:" + bytes);
			file.deleteOnExit();
			System.out.println("File deleted");
		}
		}*/
	}
	/**
	 * Write file to the disk asynchronously
	 *
	 */
/*	class MyWriter extends Thread{
		

		public MyWriter(Queue queue){
		}
		@Override
		public void run(){
			while(true){
				try {
					SigarTest.queue.write();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		
	}
	}*/
/*	*//**
	 * Read file from the disk asynchronously
	 *
	 *//*
	class MyReader extends Thread{
		
		public MyReader(Queue queue){
		}
		@Override
		public void run(){
			while(true){
				try {
					SigarTest.queue.read();
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
	}
}*/
	
	
