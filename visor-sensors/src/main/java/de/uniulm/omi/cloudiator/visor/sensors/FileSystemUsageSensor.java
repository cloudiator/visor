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

package de.uniulm.omi.cloudiator.visor.sensors;

import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.AbstractSensor;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by daniel on 26.01.16.
 */
public class FileSystemUsageSensor extends AbstractSensor {

    private final static String FILE_PATH = "file.path";
    private final static String DEFAULT_FILE_PATH = "/";
    private FileStore fileStore;

    @Override protected void initialize(MonitorContext monitorContext,
        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);

        final Path path =
            Paths.get(sensorConfiguration.getValue(FILE_PATH).orElse(DEFAULT_FILE_PATH));
        try {
            this.fileStore = Files.getFileStore(path);
        } catch (IOException e) {
            throw new SensorInitializationException(String
                .format("Could not find file store for path %s", path.toFile().getAbsolutePath()),
                e);
        }
    }

    @Override protected Measurement measureSingle() throws MeasurementNotAvailableException {

        checkState(fileStore != null, "file store was not correctly initialized");

        try {
            final double usage =
                100 - (((double) fileStore.getUsableSpace() / fileStore.getTotalSpace()) * 100);
            return measurementBuilder(Double.class).now().value(usage).build();
        } catch (IOException e) {
            throw new MeasurementNotAvailableException(e);
        }
    }
}
