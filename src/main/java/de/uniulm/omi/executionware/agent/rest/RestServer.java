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

package de.uniulm.omi.executionware.agent.rest;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.executionware.agent.rest.resources.Monitor;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
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

    @Inject
    public RestServer(@Named("localIp") String localIp, @Named("restPort") int restPort) {
        checkNotNull(localIp);
        checkArgument(!localIp.isEmpty());
        checkArgument(restPort > 0);

        URI baseUri = UriBuilder.fromUri("http://" + localIp).port(restPort).build();
        ResourceConfig config = new ResourceConfig();
        //config.packages("de.uniulm.omi.executionware.agent.rest.resources");
        config.register(new Monitor());
        try {
            GrizzlyHttpServerFactory.createHttpServer(baseUri, config).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
