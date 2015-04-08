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

package de.uniulm.omi.cloudiator.visor.telnet;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.visor.execution.ExecutionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by daniel on 15.12.14.
 */
public class SocketServer {

    private static final Logger LOGGER = LogManager.getLogger(SocketServer.class);

    @Inject
    public SocketServer(@Named("telnetPort") int port, ExecutionService executionService,
        ServerListenerFactoryInterface serverListenerFactory) {
        checkArgument(port > 0, "Argument port must be > 0");
        if (port < 1024) {
            LOGGER.warn(
                "You are running the telnet server on a port < 1024. This is usually not a good idea.");
        }
        try {
            LOGGER.info(String.format("Starting socket server on port %d", port));
            ServerSocket serverSocket = new ServerSocket(port);
            executionService.execute(serverListenerFactory.create(serverSocket));
        } catch (IOException e) {
            LOGGER.fatal("Server crashed.", e);
            System.exit(1);
        }
    }
}
