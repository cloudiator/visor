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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Return latency when connecting to the given host in milliseconds
 * @author Vadim Raskin
 *
 */
public class AverageLatencyProbe {
	public double getAverageLatence(String ip, int port) throws IOException{
		long latency = 0;
		int loopPeriod = 5;
		long startTime;
		long endTime;
		InetAddress address = InetAddress.getByName(ip); 
		SocketAddress socketAddress = new InetSocketAddress(address,port);
		// calculate average for several latency values
		for(int i = 0; i<loopPeriod; i++)
		{
			Socket s = new Socket();
			startTime = System.currentTimeMillis();
			s.connect(socketAddress);
			endTime = System.currentTimeMillis();
			latency += endTime - startTime;
			s.close();
		}
		return latency/loopPeriod;
	}

}
