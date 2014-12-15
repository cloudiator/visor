/*
 *
 *  * Copyright (c) 2014 University of Ulm
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

package de.uniulm.omi.monitoring.config.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniulm.omi.monitoring.config.api.CliConfigurationProviderInterface;
import de.uniulm.omi.monitoring.config.api.FileConfigurationProviderInterface;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by daniel on 15.12.14.
 */
@Singleton
public class FileConfigurationProvider implements FileConfigurationProviderInterface {

    private final Properties properties;

    @Inject
    public FileConfigurationProvider(final CliConfigurationProviderInterface cliConfigurationProvider) {
        this.properties = new Properties();
        try {
            properties.load(new BufferedInputStream(new FileInputStream(cliConfigurationProvider.getConfigurationFileLocation())));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read properties file.", e);
        }
    }

    @Override
    public int getExecutionThreads() {
        String executionThreads = this.properties.getProperty("executionThreads");
        checkState(executionThreads != null, "Could not read configuration value for executionThreads.");
        return Integer.parseInt(checkNotNull(executionThreads));
    }
}
