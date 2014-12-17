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

package de.uniulm.omi.monitoring.server.impl;

import com.google.inject.Inject;
import de.uniulm.omi.monitoring.config.api.ConfigurationProviderInterface;
import de.uniulm.omi.monitoring.execution.api.ExecutionServiceInterface;
import de.uniulm.omi.monitoring.server.api.ServerListenerFactoryInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by daniel on 15.12.14.
 */
public class SocketServer {

    private static final Logger logger = LogManager.getLogger(SocketServer.class);

    @Inject
    public SocketServer(ConfigurationProviderInterface configurationProvider, ExecutionServiceInterface executionService, ServerListenerFactoryInterface serverListenerFactory) {

        try {
            logger.info(String.format("Starting socket server on port %d", configurationProvider.getPort()));
            ServerSocket serverSocket = new ServerSocket(configurationProvider.getPort());
            executionService.execute(serverListenerFactory.create(serverSocket));
        } catch (IOException e) {
            logger.fatal("Server crashed.",e);
            System.exit(1);
        }
    }
}
