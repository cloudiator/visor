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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.util.Set;

/**
 * Created by daniel on 08.04.15.
 */
public class IpModule extends AbstractModule {

    @Override protected void configure() {

    }

    @Provides @Named("localIp") public String provideIp(Set<IpProvider> ipProviders) {
        for (IpProvider ipProvider : ipProviders) {
            if (ipProvider.getPublicIp() != null) {
                return ipProvider.getPublicIp();
            }
        }
        throw new ConfigurationException("Could not resolve the ip address.");
    }
}
