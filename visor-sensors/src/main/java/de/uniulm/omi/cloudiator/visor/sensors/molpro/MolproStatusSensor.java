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

package de.uniulm.omi.cloudiator.visor.sensors.molpro;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.*;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.*;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 */
public class MolproStatusSensor extends AbstractSensor {

    @Override protected void initialize(MonitorContext monitorContext, SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);
    }

    enum MolproStatus {
	    INITIALISING, // nothing happened so far
	    RUNNING, // molpro is still in the middle of executing
	    DONE, // we already reported the final results
	    FAILURE {
    		    @Override boolean isFinalState() {return true;} 
	    }, // something bad happened
	    SUCCESS{
		    @Override boolean isFinalState() {return true;} 
	    },
	    UNKNOWN,  // an error occurred
	    TO_BE_COMPLETED,
	    ;
	    boolean isFinalState(){return false;}
    }

    private static final String MOLPRO_NAME = "molpro";
    private static final String EXECUTOR_NAME = "execute.sh";

    private static final String LOG_FILE = "/home/centos/molpro/cactos/execute.log";
    private static final String SUCCESS_FILE = "/home/centos/molpro/status.complete";
    private static final String FAILURE_FILE = "/home/centos/molpro/status.failure";
    private static final String OUTPUT_FILE = "/home/centos/molpro/cactos/computation/molpro.out";
    private static final String MONITOR_FILE = "/home/centos/molpro/status.monitored";

    private static final String QUERY_STRING = "grep ";
    
    private boolean processRunning(String procName) throws InterruptedException, IOException {
	Process p = Runtime.getRuntime().exec("pa -A | grep " + procName);
	int i = p.waitFor();
	if(i == 0) return true;
	if(i == 1) return false;
	throw new IllegalStateException("grep error");
    }
    
    private boolean molproRunning() throws InterruptedException, IOException {
	    return processRunning(MOLPRO_NAME);
    }

    private boolean executorRunning() throws InterruptedException, IOException {
	    return processRunning(EXECUTOR_NAME);
    }

    private File getFile(String absFilename) {
	    return new File(absFilename);
    }

    private boolean deleteFile(String absFilename){
	    File f = getFile(absFilename);
	    return f.delete();
    }

    private boolean fileExists(String absFilename){
	    File f = getFile(absFilename);
	    return f.exists() && f.isFile();
    }

    private boolean failureFileExists() {
	    return fileExists(FAILURE_FILE);
    }

    private boolean completeFileExists() {
   	    return fileExists(MONITOR_FILE);
    }

    private boolean successFileExists() {
    	    return fileExists(OUTPUT_FILE);
    }

    private boolean outputFileExists() {
    	    return fileExists(OUTPUT_FILE);
    }

    private MolproStatus getStatus() {
	    try {
		    if(completeFileExists()) return MolproStatus.DONE;
		    if(molproRunning()) return MolproStatus.RUNNING;
		    if(failureFileExists()) return MolproStatus.FAILURE;
		    if(successFileExists()) return MolproStatus.SUCCESS;
		    // none of the critical files exist, let's see if
		    // an outputfile is there
		    if(outputFileExists()) return MolproStatus.TO_BE_COMPLETED;
		    // nothing found. probably initialising
		    return MolproStatus.INITIALISING;
	    } catch (Exception ie) {
		    ie.printStackTrace();
		    return MolproStatus.UNKNOWN;
	    }
    }

    private void copyEncodedFileToBuffer(StringBuilder b, File f) throws IOException {
	Encoder enc = Base64.getEncoder();
	byte[] by = enc.encode(Files.readAllBytes(f.toPath()));
	b.append(new String(by));
    }

    private void copyFileToBuffer(StringBuilder b, String prefix, String absFilename) throws IOException {
	b.append(prefix);
	if(fileExists(absFilename)){copyEncodedFileToBuffer(b, getFile(absFilename));}
	else b.append("\"\"");
    }

    private void touchFileFile() throws IOException {
	    File f = getFile(MONITOR_FILE);
	    f.createNewFile();
    }

    private void dealWithFinalState(StringBuilder b, MolproStatus stat) throws IOException {
	    if(!stat.isFinalState()) return;
	    copyFileToBuffer(b, ", log : ", LOG_FILE);
	    copyFileToBuffer(b, ", output : ", OUTPUT_FILE);
    	    touchFileFile();
    }

    @Override protected Measurement measureSingle() throws MeasurementNotAvailableException {
        try {
		MolproStatus status = getStatus();
		StringBuilder b = new StringBuilder();
		b.append("{ status : " ).append(status);
		dealWithFinalState(b, status);
		b.append("}");
		return MeasurementBuilder.newBuilder().
			timestamp(System.currentTimeMillis()).
                        value(b.toString()).build();
        } catch (Exception e) {
            throw new MeasurementNotAvailableException(e);
        }
    }
}

