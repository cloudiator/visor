/*
 * Copyright (c) 2014-2015 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.monitoring.sensors.logsensors;

import de.uniulm.omi.cloudiator.visor.monitoring.api.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.monitoring.api.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.sensors.AbstractSensor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zarioha
 *         This Sensor read log file from any ways with conditions
 */

public abstract class AbstractLogSensor extends AbstractSensor {

    protected String fileName;
    private long filePointer;
    private RandomAccessFile file;

    protected List<String> contains = new ArrayList<String>();
    protected List<String> dontContains = new ArrayList<String>();
    protected Pattern requestPattern;

    @Override protected void initialize() throws SensorInitializationException {
        try {
            file = new RandomAccessFile(fileName, "r");
        } catch (FileNotFoundException e) {
            throw new SensorInitializationException("Error opening file : " + fileName, e);
        }
    }

    @Override protected Measurement getMeasurement(MonitorContext monitorContext)
        throws MeasurementNotAvailableException {
        try {
            //RandomAccessFile file = new RandomAccessFile(fileName, "r");
            List<String> lines = new ArrayList<String>();
            if (file.length() < filePointer) {
                String message =
                    "Some lines were erased (chars from " + file.length() + " to " + filePointer
                        + " are missing)";
                filePointer = 0;
                throw new MeasurementNotAvailableException(message);
            } else {
                file.seek(filePointer);
                String line;
                while ((line = file.readLine()) != null) {
                    //NOTE I do not understand these conditions
                    boolean isOkLine = true;

                    for (String temp : contains) {
                        if (!line.contains(temp)) {
                            isOkLine = false;
                        }
                    }
                    for (String temp : dontContains) {
                        if (line.contains(temp)) {
                            isOkLine = false;
                        }
                    }

                    if (isOkLine) {
                        if (requestPattern != null) {
                            Matcher requestMatcher = requestPattern.matcher(line);
                            while (requestMatcher.find()) {
                                String temp = "";
                                for (int i = 1; i <= requestMatcher.groupCount(); i++) {
                                    temp = temp + requestMatcher.group(i);
                                    if (i != requestMatcher.groupCount())
                                        temp = temp + ", ";
                                }
                                lines.add("{" + temp + "}");
                            }
                        } else {
                            lines.add(line);
                        }
                    }
                }
                filePointer = file.getFilePointer();
                return new MeasurementImpl(System.currentTimeMillis(), lines);
            }

            //file.close();
        } catch (IOException e) {
            throw new MeasurementNotAvailableException("Error accessing file : " + fileName, e);
        }
    }
}
