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

import org.hyperic.sigar.NetInterfaceStat;
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

public class NetworkStateProbe implements Sensor{
	
	private static int SMALL_CYCLE= 1000, BIG_CYCLE= 5000, BINARY_NUMBER= 1024, THOUSAND= 1000, EIGHT=8;
	private static double PERCENTAGE= 100.0;
	Sigar sigarImpl;
	SigarProxy sigar;
	public NetworkStateProbe(){
		this.sigarImpl = new Sigar();
		
	}
	/**
	 * Provide channel width in Mbit/sec, retrieve used bandwidth in percentage from this value.
	 * Receiver bandwidth is considered here
	 * @param channelWidth
	 * @return
	 * @throws InterruptedException 
	 * @throws SigarException 
	 */
	public double getAverageUsedDownloadBandwidth(int channelWidth) throws SigarException, InterruptedException{
		double percentage = PERCENTAGE;
		// make both values of the same scope, KBytes/s
		double channelWidthInKBytesPerSecond = ((channelWidth/EIGHT)*BINARY_NUMBER);
		double rxRateInKBytesPerSecond = this.getAverageRxRate()/BINARY_NUMBER;
		percentage = (rxRateInKBytesPerSecond*percentage)/channelWidthInKBytesPerSecond;
		
		// round to 3 symbols after the dot
		int roundedValue = (int) (percentage*THOUSAND); 
		percentage = (double) roundedValue/THOUSAND;
		return percentage;
	}
	/**
	 * Provide channel width in Mbit/sec, retrieve used bandwidth in percentage from this value.
	 * Transmitter bandwidth is considered here
	 * @param channelWidth
	 * @return
	 * @throws InterruptedException 
	 * @throws SigarException 
	 */
	public double getAverageUsedUploadBandwidth(int channelWidth) throws SigarException, InterruptedException{
		double percentage = PERCENTAGE;
		// make both values of the same scope, KBytes/s
		double channelWidthInKBytesPerSecond = ((channelWidth/EIGHT)*BINARY_NUMBER);
		double rxRateInKBytesPerSecond = this.getAverageTxRate()/BINARY_NUMBER;
		percentage = (rxRateInKBytesPerSecond*percentage)/channelWidthInKBytesPerSecond;
		
		// round to 3 symbols after the dot
		int roundedValue = (int) (percentage*THOUSAND); 
		percentage = (double) roundedValue/THOUSAND;
		return percentage;
	}
	/**
	 * Average rate of received bytes in bytes per second. Blocking method, execute it in a separate thread
	 * @return
	 * @throws SigarException
	 * @throws InterruptedException
	 */
	public double getAverageRxRate() throws SigarException, InterruptedException
	{
		int smallCycle = SMALL_CYCLE;
		int bigCycle = BIG_CYCLE;
		NetInterfaceStat netStat = null;
		long rxBytesLastCycle = 0;
		long rxBytesNewCycle = 0;
		ArrayList<Long> rxBytesTotal = new ArrayList<Long>();
		long averageBytesPerSecond; 
		
		// measure received bytes n times, where n is bigCycle/smallCycle
		for(int i=0;i<=bigCycle; i+=smallCycle)
		{
			rxBytesNewCycle = 0;
			this.sigar=SigarProxyCache.newInstance(sigarImpl);
			
			// measure the number of received bytes on all network interfaces
			for(String ni : sigar.getNetInterfaceList())
			{
				netStat = this.sigar.getNetInterfaceStat(ni);
				if(i==0)
					rxBytesLastCycle+=netStat.getRxBytes();
				else
					rxBytesNewCycle+=netStat.getRxBytes();
			}
			// we are interested on the changed values
			if(rxBytesNewCycle - rxBytesLastCycle > 0)
			{
				rxBytesTotal.add(rxBytesNewCycle-rxBytesLastCycle);
				rxBytesLastCycle = rxBytesNewCycle;
			}
			// sleep till the next measure cycle
			Thread.sleep(smallCycle);
		}
		// get average value for all non zero measurements
		averageBytesPerSecond = this.calculateAverageRate(rxBytesTotal);
		
		return averageBytesPerSecond;
	}
	/**
	 * Average rate of transmitted bytes in bytes per second. Blocking method, execute it in a separate thread
	 * @return
	 * @throws SigarException
	 * @throws InterruptedException
	 */
	public double getAverageTxRate() throws SigarException, InterruptedException
	{
		int smallCycle = SMALL_CYCLE;
		int bigCycle = BIG_CYCLE;
		NetInterfaceStat netStat = null;
		long txBytesLastCycle = 0;
		long txBytesNewCycle = 0;
		ArrayList<Long> txBytesTotal = new ArrayList<Long>();
		long averageBytesPerSecond; 
		
		// measure transmitted bytes during bigCycle period
		for(int i=0;i<=bigCycle; i+=smallCycle)
		{
			txBytesNewCycle = 0;
			this.sigar=SigarProxyCache.newInstance(sigarImpl);
			
			// measure the number of received bytes on all interfaces
			for(String ni : sigar.getNetInterfaceList())
			{
				netStat = this.sigar.getNetInterfaceStat(ni);
				if(i==0)
					txBytesLastCycle+=netStat.getTxBytes();
				else
					txBytesNewCycle+=netStat.getTxBytes();
			}
			// we are interested on the changed values
			if(txBytesNewCycle - txBytesLastCycle > 0)
			{
				txBytesTotal.add(txBytesNewCycle-txBytesLastCycle);
				txBytesLastCycle = txBytesNewCycle;
			}
				
			Thread.sleep(smallCycle);
		}
		// get average value for all non zero measurements
		averageBytesPerSecond = this.calculateAverageRate(txBytesTotal);
		
		return averageBytesPerSecond;
	}
	//TODO: substitute by some more beautiful calculation
	private long calculateAverageRate(ArrayList<Long> measurements){
		long result=0;
		for(Long m : measurements)
			result += m;
		if(result!=0)
			result = result/measurements.size();
		return result;
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
		double averageRxRate = 0, averageTxRate = 0;
		try {
			averageRxRate = getAverageRxRate();
			averageTxRate = getAverageTxRate();
		} catch (InterruptedException | SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(averageRxRate <= 0 || averageTxRate <= 0) {
	            throw new MeasurementNotAvailableException("Network metric rate isn´t available");
	    }
		
		return new MeasurementImpl(System.currentTimeMillis(), ("Avg receive rate is "+averageRxRate/1024 + " kBytes/sec, " + 
				"Avg transmit rate is "+averageTxRate/1024 + " kBytes/sec"));
	}
}


