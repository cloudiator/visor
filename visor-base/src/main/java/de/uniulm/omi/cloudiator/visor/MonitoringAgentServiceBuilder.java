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
import de.uniulm.omi.cloudiator.visor.config.api.IpProvider;
import de.uniulm.omi.cloudiator.visor.config.impl.BaseConfigurationModule;
import de.uniulm.omi.cloudiator.visor.config.impl.FileConfigurationAccessor;
import de.uniulm.omi.cloudiator.visor.server.config.ServerModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by daniel on 17.12.14.
 */
public class MonitoringAgentServiceBuilder {

    private FileConfigurationAccessor fileConfigurationAccessor;
    private List<IpProvider> ipProviderList;
    private Set<Module> modules;
    private static final Logger LOGGER = LogManager.getLogger(MonitoringAgentServiceBuilder.class);

    private MonitoringAgentServiceBuilder() {
        this.modules = new HashSet<>();
        this.ipProviderList = new LinkedList<>();
    }

    public static MonitoringAgentServiceBuilder createNew() {
        return new MonitoringAgentServiceBuilder();
    }

    public MonitoringAgentServiceBuilder confFilePath(String confFilePath) {
        this.fileConfigurationAccessor = new FileConfigurationAccessor(confFilePath);
        return this;
    }

    public MonitoringAgentServiceBuilder addIpProvider(IpProvider ipProvider) {
        this.ipProviderList.add(ipProvider);
        return this;
    }

    public MonitoringAgentServiceBuilder loadModules(Module... modules) {
        this.modules.addAll(Arrays.asList(modules));
        return this;
    }

    protected void loadReportingModuleBasedOnPropertiesFile()
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final String reportingModule =
            this.fileConfigurationAccessor.getProperty("reportingModule");
        checkState(reportingModule != null);
        this.modules.add((Module) Class.forName(reportingModule).newInstance());
    }

    protected String resolvePublicIpAddress() {
        for (IpProvider ipProvider : ipProviderList) {
            if (ipProvider.getPublicIp() != null) {
                return ipProvider.getPublicIp();
            }
        }
        throw new IllegalStateException("Could not find any public ip address.");
    }

    public MonitoringAgentService build() {
        try {
            this.loadReportingModuleBasedOnPropertiesFile();
            this.modules.add(new ServerModule());
            this.modules.add(new BaseConfigurationModule(this.fileConfigurationAccessor,
                this.resolvePublicIpAddress()));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalStateException e) {
            LOGGER.fatal(e);
            System.exit(1);
        }

        return new MonitoringAgentService(this.modules);
    }


}
