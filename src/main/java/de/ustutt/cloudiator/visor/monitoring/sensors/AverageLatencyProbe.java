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
package de.ustutt.cloudiator.visor.monitoring.sensors;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.google.common.base.Optional;

import de.uniulm.omi.executionware.agent.monitoring.api.InvalidMonitorContextException;
import de.uniulm.omi.executionware.agent.monitoring.api.Measurement;
import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.api.Sensor;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;

/**
 * Return latency when connecting to the given host in milliseconds
 */
public class AverageLatencyProbe implements Sensor{
	
	
	public double getAverageLatence() throws IOException{
		long latency = 0;
		long startTime;
		long endTime;
		InetAddress address = InetAddress.getByName(MonitorContext.PING_IP); 
		SocketAddress socketAddress = new InetSocketAddress(address,Integer.valueOf(MonitorContext.PING_PORT));
		// calculate average for several latency values
		for(int i = 0; i<Integer.valueOf(MonitorContext.PING_LOOP); i++)
		{
			Socket s = new Socket();
			startTime = System.currentTimeMillis();
			s.connect(socketAddress);
			endTime = System.currentTimeMillis();
			latency += endTime - startTime;
			s.close();
		}
		return latency/Integer.valueOf(MonitorContext.PING_LOOP);
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
		double val = 0;
		 try {
			val = getAverageLatence();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (val <= 0) {
	        throw new MeasurementNotAvailableException("Latency Calculation isn´t available");
	    }
		return new MeasurementImpl(System.currentTimeMillis(), val);
	}
}
