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

package de.uniulm.omi.cloudiator.visor;

import com.google.inject.Module;
import de.uniulm.omi.cloudiator.visor.config.*;
import de.uniulm.omi.cloudiator.visor.exceptions.ConfigurationException;
import de.uniulm.omi.cloudiator.visor.rest.RestServerModule;
import de.uniulm.omi.cloudiator.visor.telnet.TelnetServiceModule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 17.12.14.
 */
public class VisorServiceBuilder {

    private String[] args;
    private Set<Module> modules;

    private VisorServiceBuilder() {
        this.modules = new HashSet<>();
    }

    public static VisorServiceBuilder create() {
        return new VisorServiceBuilder();
    }

    public VisorServiceBuilder args(String[] args) {
        checkNotNull(args);
        this.args = args;
        return this;
    }

    public VisorServiceBuilder modules(Module... modules) {
        checkNotNull(modules);
        this.modules.addAll(Arrays.asList(modules));
        return this;
    }

    private void loadModulesBasedOnConfiguration(ConfigurationAccess configurationAccess) {
        try {
            this.modules.add((Module) Class
                .forName(configurationAccess.getProperties().getProperty("reportingModule"))
                .newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new ConfigurationException(e);
        }
    }

    public VisorService build() {
        //create the config file access
        CommandLinePropertiesAccessor commandLinePropertiesAccessor =
            new CommandLinePropertiesAccessorImpl(this.args);
        ConfigurationAccess configurationAccess =
            new FileConfigurationAccessor(commandLinePropertiesAccessor.getConfFileLocation());
        this.modules.add(new BaseModule(configurationAccess, commandLinePropertiesAccessor));
        this.modules.add(new TelnetServiceModule());
        this.modules.add(new RestServerModule());
        this.modules.add(new InitModule());
        this.loadModulesBasedOnConfiguration(configurationAccess);
        return new VisorService(this.modules);
    }


}
