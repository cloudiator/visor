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

package de.uniulm.omi.cloudiator.visor.rest;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.visor.monitoring.api.MonitoringService;
import de.uniulm.omi.cloudiator.visor.rest.controllers.MonitorController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 06.02.15.
 */
public class RestServer {

    private static final Logger LOGGER = LogManager.getLogger(RestServer.class);

    @Inject public RestServer(@Named("restPort") int restPort, @Named("restHost") String restHost,
        MonitoringService monitoringService) {
        checkArgument(restPort > 0);

        if (restPort <= 1024) {
            LOGGER.warn("You try to open a port below 1024. This is usual not a good idea...");
        }

        checkNotNull(restHost);
        checkArgument(!restHost.isEmpty());

        URI baseUri = UriBuilder.fromUri(restHost).port(restPort).build();
        ResourceConfig config = new ResourceConfig();
        config.register(new MonitorController(monitoringService));
        config.register(MoxyJsonFeature.class);
        try {
            GrizzlyHttpServerFactory.createHttpServer(baseUri, config).start();
        } catch (IOException e) {
            LOGGER.fatal("Could not start rest server.", e);
            System.exit(1);
        }
    }
}
