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

package de.uniulm.omi.monitoring;

import com.google.inject.Module;
import de.uniulm.omi.monitoring.config.file.FileConfigurationAccessor;
import de.uniulm.omi.monitoring.config.impl.BaseConfigurationModule;
import de.uniulm.omi.monitoring.server.config.ServerModule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by daniel on 17.12.14.
 */
public class MonitoringAgentServiceBuilder {

    private FileConfigurationAccessor fileConfigurationAccessor;
    private String ip;
    private Set<Module> modules;

    private MonitoringAgentServiceBuilder() {
        this.modules = new HashSet<>();
    }

    public static MonitoringAgentServiceBuilder createNew() {
        return new MonitoringAgentServiceBuilder();
    }

    public MonitoringAgentServiceBuilder confFilePath(String confFilePath) {
        this.fileConfigurationAccessor = new FileConfigurationAccessor(confFilePath);
        return this;
    }

    public MonitoringAgentServiceBuilder ip(String ip) {
        this.ip = ip;
        return this;
    }

    public MonitoringAgentServiceBuilder loadModules(Module... modules) {
        this.modules.addAll(Arrays.asList(modules));
        return this;
    }

    protected void loadReportingModuleBasedOnPropertiesFile() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final String reportingModule = this.fileConfigurationAccessor.getProperty("reportingModule");
        checkState(reportingModule != null);
        this.modules.add((Module) Class.forName(reportingModule).newInstance());
    }

    public MonitoringAgentService build() {
        try {
            this.loadReportingModuleBasedOnPropertiesFile();
            this.modules.add(new ServerModule());
            this.modules.add(new BaseConfigurationModule(this.fileConfigurationAccessor, this.ip));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.exit(1);
        }

        return new MonitoringAgentService(this.modules);
    }


}
