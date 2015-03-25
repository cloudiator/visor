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

import java.util.ArrayList;

import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;
/**
 * Instance of this class will measure the IO load in the system. It is run in a separate thread.
 * IO overhead is presented by read/write requests per second.
 * Do measurements in two subthreads
 * @author Vadim Raskin
 *
 */
public class IOLoadProbe extends Thread{

	Sigar sigarImpl;
	SigarProxy sigar;
	long writeRequests;
	long readRequests;
	double writeSpeed;
	double readSpeed;
	int measurePeriod;
	boolean mark = true;
	long averageNumberOfReads;
	long averageNumberOfWrites;
	String fsRoot;
	ReadCalculator readCalculator;
	WriteCalculator writeCalculator;
	
	public IOLoadProbe(String fsRoot,int measurePeriod){
		this.sigarImpl = new Sigar();
		this.sigar=SigarProxyCache.newInstance(sigarImpl);
		writeRequests = 0;
		readRequests = 0;
		writeSpeed = 0;
		readSpeed = 0;
		averageNumberOfReads = 0;
		this.fsRoot = fsRoot;
		this.measurePeriod = measurePeriod;
		
	}
	
	@Override
	public void run() {
		
		// start two threads that measure number of read/write requests
		// parallel tasks will finish faster, right?
		this.readCalculator = new ReadCalculator(fsRoot, measurePeriod);
		this.writeCalculator = new WriteCalculator(fsRoot,measurePeriod);
		readCalculator.start();
		writeCalculator.start();
		// calculate I/O load until someone interrupts us
		while(true)
		{
			// do not busy wait, sleep until first results are ready
			// TODO: implement future object
			try {
				Thread.sleep(measurePeriod);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			averageNumberOfReads = readCalculator.getAverageNumberOfReads();
			System.out.println("Average number of reads: "+ averageNumberOfReads);	
			averageNumberOfWrites = writeCalculator.getAverageNumberOfWrites();
			System.out.println("Average number of writes: "+ averageNumberOfWrites);	
			
		}
	}
}
/**
 * Object of this class calculates the number of read requests
 * Runs in infinite loop until external interrupt occurs
 * @author raskin
 *
 */
class ReadCalculator extends Thread{
	Sigar sigarImpl;
	SigarProxy sigar;
	long averageNumberOfReads = 0;
	long averageNumberOfWrites = 0;
	int measurePeriod = 5000;
	String fsRoot;
	
	public ReadCalculator(String fsRoot, int measurePeriod){
		this.sigarImpl = new Sigar();
		this.sigar=SigarProxyCache.newInstance(sigarImpl);
		this.fsRoot = fsRoot;
		this.measurePeriod = measurePeriod;
		this.setName("Read/Write counter");
	}
	public void run(){
		while(true){
			try {
				averageNumberOfReads = this.calculateAverageNumberOfReads(measurePeriod, fsRoot);
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public long getAverageNumberOfReads(){
		return this.averageNumberOfReads;
	}
	private long calculateAverageNumberOfReads(int measurePeriod, String fsRoot) throws SigarException, InterruptedException
	{
		long totalNumberOfRequests = 0;
		int smallCyclePeriod = 1000;
		ArrayList<Long> measurements = new ArrayList<Long>();
		for(int i=0;i<measurePeriod; i+=smallCyclePeriod){
			long intermediateResult = this.getReadRequestsPerSecond(fsRoot);
			measurements.add(intermediateResult);
			totalNumberOfRequests+=intermediateResult;
		}
		return totalNumberOfRequests/measurements.size();
	}
	/**
	 * Measure number of read requests per one second
	 * @param fsRoot
	 * @return
	 * @throws SigarException
	 * @throws InterruptedException
	 */
	private long getReadRequestsPerSecond(String fsRoot) throws SigarException, InterruptedException
	{
		int milis = 1000;
		long numberOfReads = 0;
		long readRequestsPerSecond = 0;
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		FileSystemUsage fsUsage = sigar.getFileSystemUsage(fsRoot);
		long initNumberOfReads = fsUsage.getDiskReads();
		Thread.sleep(milis);
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		fsUsage = sigar.getFileSystemUsage(fsRoot);
		numberOfReads = fsUsage.getDiskReads();
		readRequestsPerSecond = numberOfReads - initNumberOfReads;
		if(readRequestsPerSecond < 0){
			System.out.println("Value can not be negative!");
			readRequestsPerSecond = 0;
		}	
		return readRequestsPerSecond;
	}
//	public double getAverageReadSpeed(String fsRoot) throws SigarException, InterruptedException
//	{
//		int measureInterval = 500;
//		int counter = 0;
//		ArrayList<Long> bytesOnEachInterval = new ArrayList<Long>();
//		FileSystemUsage fsUsage = null;
//		long initReadBytes = 0;
//		long readBytes = 0;
//		long speedChange = 0;
//		
//		fsUsage = sigar.getFileSystemUsage(fsRoot);
//		initReadBytes = fsUsage.getDiskReadBytes();
//		// save the number of read bytes over each measure interval
//		for (int i = counter;i<=measurePeriod; i+=measureInterval){
//			Thread.sleep(measureInterval);
//			System.out.println("Measuring cycle...");
//			this.sigar = SigarProxyCache.newInstance(sigarImpl);
//			fsUsage = sigar.getFileSystemUsage(fsRoot);
//			readBytes = fsUsage.getDiskReadBytes();
//			if(readBytes > initReadBytes){
//				speedChange = readBytes - initReadBytes;
//				bytesOnEachInterval.add(speedChange);
//				initReadBytes = readBytes;
//				System.out.println("Speed change in the interval: " + speedChange);
//			}
//			counter+=measureInterval;
//		}
//		return this.getAverageSpeed(bytesOnEachInterval, measureInterval);
//	}
}
/**
 * Object of this class calculates the average number of write requests during 
 * measure period. Runs in infinite loop until interrupted externally
 * @author raskin
 *
 */
class WriteCalculator extends Thread{
	Sigar sigarImpl;
	SigarProxy sigar;
	long averageNumberOfReads = 0;
	long averageNumberOfWrites = 0;
	int measurePeriod = 5000;
	String fsRoot;
	
	public WriteCalculator(String fsRoot, int measurePeriod){
		this.sigarImpl = new Sigar();
		this.sigar=SigarProxyCache.newInstance(sigarImpl);
		this.fsRoot = fsRoot;
		this.measurePeriod = measurePeriod;
		this.setName("Write counter");
	}
	public void run(){
		while(true){
			try {
				averageNumberOfWrites = this.calculateAverageNumberOfWrites(measurePeriod, fsRoot);
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public long getAverageNumberOfWrites(){
		return this.averageNumberOfWrites;
	}
	private long calculateAverageNumberOfWrites(int measurePeriod, String fsRoot) throws SigarException, InterruptedException
	{
		long totalNumberOfRequests = 0;
		int smallCyclePeriod = 1000;
		ArrayList<Long> measurements = new ArrayList<Long>();
		for(int i=0;i<measurePeriod; i+=smallCyclePeriod){
			long intermediateResult = this.getWriteRequestsPerSecond(fsRoot);
			measurements.add(intermediateResult);
			totalNumberOfRequests+=intermediateResult;
		}
		return totalNumberOfRequests/measurements.size();
	}
	/**
	 * Measure number of write requests per one second
	 * @param fsRoot
	 * @return
	 * @throws SigarException
	 * @throws InterruptedException
	 */
	public long getWriteRequestsPerSecond(String fsRoot) throws SigarException, InterruptedException
	{
		int milis = 1000;
		long numberOfWrites = 0;
		long writeRequestsPerSecond = 0;
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		FileSystemUsage fsUsage = sigar.getFileSystemUsage(fsRoot);
		long initNumberOfWrites = fsUsage.getDiskWrites();
//		System.out.println("Initial number of write operations is "+Long.toString(initNumberOfWrites));
		Thread.sleep(milis);
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		fsUsage = this.sigar.getFileSystemUsage(fsRoot);
		numberOfWrites = fsUsage.getDiskWrites();
//		System.out.println("Number of write operations after writing the file "+Long.toString(numberOfWrites));
		writeRequestsPerSecond = numberOfWrites - initNumberOfWrites;
		if(writeRequestsPerSecond < 0){
			System.out.println("Value can not be negative!");
			writeRequestsPerSecond = 0;
		}	
		return writeRequestsPerSecond;
	}
}
