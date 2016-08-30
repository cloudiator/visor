/*
 * Copyright (c) 2014-2016 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.reporting.chukwa;

import de.uniulm.omi.cloudiator.visor.monitoring.Metric;

import java.util.concurrent.atomic.AtomicLong;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MetricToChukwa implements Function<Metric, String> {

	private static final Logger LOGGER = LogManager.getLogger(ChukwaReporter.class);
	private static Map<String, AtomicLong> streamCounterCache = new HashMap<>(); 
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    private static final int DEFAULT_PROTOCOL_VERSION = 1;
    private final String vmUuid;

    MetricToChukwa(String vmUuid) {
        this.vmUuid = vmUuid;
    }
    
    @Override public String apply(Metric metric) {


        return "VMID" +
            "\t" +
            metric.getName() +
            "\t" +
            metric.getName() + "capturedTimestamp" +
            "\t" +
            metric.getName() + "tags" +
            "\n" +
            vmUuid +
            "\t" +
            metric.getValue() +
            "\t" +
            metric.getTimestamp() +
            "\t" +
            metric.getTags().toString();
    }
    
    private static final String CHUKWA_SOURCE_TAG = "chukwa.source";
    private static final String CHUKWA_DATATYPE_TAG = "chukwa.datatype";
    
    private static boolean isNonEmptyString(String s) {
    	return s != null && !s.isEmpty();
    }
    
    private static String getSource(Map<String, String> tags) {
    	String tmp = tags.get(CHUKWA_SOURCE_TAG);
    	if(isNonEmptyString(tmp)) return tmp;
    	
    	try {
			tmp = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			tmp = "";
		}
    	if(isNonEmptyString(tmp) && 
    			!"localhost".equals(tmp) && 
    			! "127.0.0.1".equals(tmp)) return tmp;
    	
    	return "Visor";
    }
    
    private static String getDatatype(Map<String, String> tags) {
    	String tmp = tags.get(CHUKWA_DATATYPE_TAG);
    	if(isNonEmptyString(tmp)) return tmp;
    	
    	return "Visor";
    }
    
    private static final String CHUKWA_PRE = "chukwa.";
    private static String buildTaglist(Map<String,String> tags) {
    	StringBuilder result = new StringBuilder();
    	// for now, only deal with chukwa tags(?)
    	for(Entry<String,String> e : tags.entrySet()) {
    		String key = e.getKey();
    		if(key == null) continue;
    		if(key.startsWith(CHUKWA_PRE) && 
    				! CHUKWA_DATATYPE_TAG.equals(key) &&
    				! CHUKWA_SOURCE_TAG.equals(key) &&
    				key.length() > CHUKWA_PRE.length()) {
    			addElement(key.substring(CHUKWA_PRE.length()), e.getValue(), result);
    		}
    	}
    	return result.toString();
    }
    
    private static void addElement(String key, String value, StringBuilder acc){
    	if(key.isEmpty()) return;
    	if(acc.length()  != 0) {
    		acc.append(",");
    	}
    	acc.append(key).append("=\"").append(value).append("\"");
    }
    
    private static String generateDebuggingInfo(long timestamp) {
    	Date d = new Date(timestamp); 
    	String date = sdf.format(d);
    	LOGGER.debug(String.format("Setting timestamp: %s", date));
    	return date;
    }

	public static ChukwaRequest parse(String vmID, Metric item) {
		LOGGER.debug(String.format("Chukwaing metric %s with tags: %s", item,
                item.getTags()));
		
		final String source = getSource(item.getTags());
		final String datatype = getDatatype(item.getTags());
		final String stream = item.getName();
		final long seqId = getLongId(stream);
		final String tagList = buildTaglist(item.getTags());
		ChukwaRequest chukwaRequest = ChukwaRequestBuilder.newBuilder().numberOfEvents(1).
	            protocolVersion(DEFAULT_PROTOCOL_VERSION).source(source).dataType(datatype).
	            stringData(item.getValue().toString()).streamName(stream).sequenceId(seqId).
	            tags(tagList).debuggingInfo(generateDebuggingInfo(item.getTimestamp())).
	            numberOfRecords(1).build();
		// .streamName("Visor Monitoring Information")
		
		 LOGGER.debug(String.format("Encoded metric %s as chukwa request %s", item,
		                chukwaRequest.toString()));
		return chukwaRequest;
	}
	
	private synchronized static long getLongId(String streamName) {
		AtomicLong c = streamCounterCache.get(streamName);
		if(c == null) {
			streamCounterCache.put(streamName, new AtomicLong(1));
			return 1L;
		}
		return c.incrementAndGet();
	}
}
