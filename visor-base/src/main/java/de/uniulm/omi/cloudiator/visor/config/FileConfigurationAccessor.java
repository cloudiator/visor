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

package de.uniulm.omi.cloudiator.visor.config;

import com.google.inject.Singleton;
import de.uniulm.omi.cloudiator.visor.exceptions.ConfigurationException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by daniel on 15.12.14.
 */
@Singleton public class FileConfigurationAccessor implements ConfigurationAccess {

    private final Properties properties;

    public FileConfigurationAccessor(String configurationFilePath) {
        this.properties = new Properties();
        try (final FileInputStream fileInputStream = new FileInputStream(configurationFilePath);
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(
                fileInputStream)) {
            properties.load(bufferedInputStream);
        } catch (IOException e) {
            throw new ConfigurationException("Could not read properties file.", e);
        }
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    @Override public Properties getProperties() {
        return this.properties;
    }
}
