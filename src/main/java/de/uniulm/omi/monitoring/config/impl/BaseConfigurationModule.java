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

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import de.uniulm.omi.monitoring.config.cli.CommandLinePropertiesAccessor;
import de.uniulm.omi.monitoring.config.file.FileConfigurationAccessor;

/**
 * Created by daniel on 17.12.14.
 */
public class BaseConfigurationModule extends AbstractModule {

    private final FileConfigurationAccessor fileConfiguration;
    private final CommandLinePropertiesAccessor commandLineProperties;

    public BaseConfigurationModule(String[] args) {
        this.commandLineProperties = new CommandLinePropertiesAccessor(args);
        this.fileConfiguration = new FileConfigurationAccessor(commandLineProperties.getConfFileLocation());
    }

    @Override
    protected void configure() {
        Names.bindProperties(binder(), fileConfiguration.getProperties());
        bindConstant().annotatedWith(Names.named("localIp")).to(this.commandLineProperties.getLocalIp());
        bindConstant().annotatedWith(Names.named("telnetPort")).to(this.commandLineProperties.getPort());
    }
}
