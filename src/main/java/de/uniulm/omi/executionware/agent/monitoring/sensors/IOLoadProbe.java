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
import org.hyperic.sigar.cmd.Iostat;

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
 *
 */
public class IOLoadProbe extends Thread implements Sensor{

	Sigar sigarImpl;
	SigarProxy sigar;
	int measurePeriod;
	long numberOfReads,numberOfBytesRead;
	long numberOfWrites, numberOfBytesWrite;
	String fsRoot;
	//ReadCalculator readCalculator;
	//WriteCalculator writeCalculator;
	/**
	 * Shared synchronized queue to store results of the measurements
	 */
	//protected static MeasurementQueue queue;
	
	public IOLoadProbe(String fsRoot,int measurePeriod){
		this.sigarImpl = new Sigar();
		this.sigar=SigarProxyCache.newInstance(sigarImpl);
		this.fsRoot = fsRoot;
		this.measurePeriod = measurePeriod;
		//queue = new MeasurementQueue(fsRoot);
		
	}
	
	public String outputDisk(String name) throws SigarException {
		 DiskUsage disk = this.sigar.getDiskUsage(name);
		 //Iostat ss = new Iostat();
		 //ss.outputDisk(fsRoot);
		 return "Reads-bytes: " + Sigar.formatSize(disk.getReadBytes()) + " | Writes-bytes: " + Sigar.formatSize(disk.getWriteBytes()) 
				 + " | Reads: " + disk.getReads() + " | Writes: " + disk.getWrites();
	}
	
	
/*	@Override
	public void run() {
		
		// start two threads that measure number of read/write requests

		this.readCalculator = new ReadCalculator(measurePeriod);
		this.writeCalculator = new WriteCalculator(measurePeriod);
		
		readCalculator.start();
		writeCalculator.start();
		// calculate I/O load until someone interrupts us
		while(true)
		{	
			try {
				//averageNumberOfReads = readCalculator.getAverageNumberOfReads();
				averageNumberOfReads = queue.getReadMeasurement();
				System.out.println("Average number of reads per "+ this.measurePeriod+ " ms: "+ averageNumberOfReads);	
				//averageNumberOfWrites = writeCalculator.getAverageNumberOfWrites();
				averageNumberOfWrites = queue.getWriteMeasurement();
				System.out.println("Average number of writes per "+ this.measurePeriod+ " ms: "+ averageNumberOfWrites);	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}*/

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
		return new MeasurementImpl(System.currentTimeMillis(), diskIO);
		
	}
}
/**
 * Implementation of synchronized queue to read and write measurements 
 * @author raskin
 *
 */
/*class MeasurementQueue{
	String fsRoot;
	// maximal number of read/write measurements that can be stored in the queue
	int maxMeasurements = 1;
	int readMeasurements = 0;
	int writeMeasurements = 0;
	long averageReads = 0;
	long averageWrites = 0;
	public MeasurementQueue(String fsRoot){
		this.fsRoot = fsRoot;
	}
	public synchronized void setReadMeasurement(long numberOfReads) throws InterruptedException{
		while(readMeasurements >= maxMeasurements){
			wait();
		}
		// save average reads here
		this.averageReads = numberOfReads;
		readMeasurements++;
		notifyAll();
	}
	public synchronized long getReadMeasurement() throws InterruptedException{
		while(readMeasurements == 0){
			wait();
		}
		// read the value
		readMeasurements--;
		long safeValue = averageReads;
		notifyAll();
		return safeValue;
		
	}
	public synchronized void setWriteMeasurement(long numberOfWrites) throws InterruptedException{
		while(writeMeasurements >=maxMeasurements){
			wait();
		}
		// save average writes here
		this.averageWrites = numberOfWrites;
		writeMeasurements++;
		notifyAll();
	}
	public synchronized long getWriteMeasurement() throws InterruptedException{
		while(writeMeasurements == 0){
			wait();
		}
		// get average reads here
		writeMeasurements--;
		// save the value here to avoid dirty read
		long safeValue = averageWrites;
		notifyAll();
		return safeValue;
	}
	public String getFsRoot(){
		return this.fsRoot;
	}
}*/
/**
 * Object of this class calculates the number of read requests
 * Runs in infinite loop until external interrupt occurs
 * @author raskin
 *
 */
/*class ReadCalculator extends Thread{
	Sigar sigarImpl;
	SigarProxy sigar;
	long averageNumberOfReads = 0;
	long averageNumberOfWrites = 0;
	int measurePeriod = 5000;
	String fsRoot;
	
	public ReadCalculator(int measurePeriod){
		this.sigarImpl = new Sigar();
		this.sigar=SigarProxyCache.newInstance(sigarImpl);
		this.fsRoot = IOLoadProbe.queue.getFsRoot();
		this.measurePeriod = measurePeriod;
		this.setName("Read/Write counter");
	}
	public void run(){
		while(true){
			try {
				//averageNumberOfReads = this.calculateAverageNumberOfReads(measurePeriod, fsRoot);
				IOLoadProbe.queue.setReadMeasurement(this.calculateAverageNumberOfReads(measurePeriod, fsRoot));
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	*//**
	 * Measure number of read requests per one second
	 * @param fsRoot
	 * @return
	 * @throws SigarException
	 * @throws InterruptedException
	 *//*
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
}*/
/**
 * Object of this class calculates the average number of write requests during 
 * measure period. Runs in infinite loop until interrupted externally
 * @author raskin
 *
 */
/*class WriteCalculator extends Thread{
	Sigar sigarImpl;
	SigarProxy sigar;
	long averageNumberOfReads = 0;
	long averageNumberOfWrites = 0;
	int measurePeriod = 5000;
	String fsRoot;
	
	public WriteCalculator(int measurePeriod){
		this.sigarImpl = new Sigar();
		this.sigar=SigarProxyCache.newInstance(sigarImpl);
		this.fsRoot = IOLoadProbe.queue.getFsRoot();
		this.measurePeriod = measurePeriod;
		this.setName("Write counter");
	}
	public void run(){
		while(true){
			try {
				//averageNumberOfWrites = this.calculateAverageNumberOfWrites(measurePeriod, fsRoot);
				IOLoadProbe.queue.setWriteMeasurement(this.calculateAverageNumberOfWrites(measurePeriod, fsRoot));
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	*//**
	 * Measure number of write requests per one second
	 * @param fsRoot
	 * @return
	 * @throws SigarException
	 * @throws InterruptedException
	 *//*
	public long getWriteRequestsPerSecond(String fsRoot) throws SigarException, InterruptedException
	{
		int milis = 1000;
		long numberOfWrites = 0;
		long writeRequestsPerSecond = 0;
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		FileSystemUsage fsUsage = sigar.getFileSystemUsage(fsRoot);
		long initNumberOfWrites = fsUsage.getDiskWrites();
		Thread.sleep(milis);
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		fsUsage = this.sigar.getFileSystemUsage(fsRoot);
		numberOfWrites = fsUsage.getDiskWrites();
		writeRequestsPerSecond = numberOfWrites - initNumberOfWrites;
		if(writeRequestsPerSecond < 0){
			System.out.println("Value can not be negative!");
			writeRequestsPerSecond = 0;
		}	
		return writeRequestsPerSecond;
	}
}*/
