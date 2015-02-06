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

package de.uniulm.omi.executionware.agent.server.impl;

import de.uniulm.omi.executionware.agent.execution.api.ExecutionServiceInterface;
import de.uniulm.omi.executionware.agent.server.api.SocketWorkerFactoryInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by daniel on 15.12.14.
 */
public class ServerListener implements Runnable {

    private final ServerSocket serverSocket;
    private final ExecutionServiceInterface executionService;
    private final SocketWorkerFactoryInterface socketWorkerFactory;
    private static final Logger logger = LogManager.getLogger(SocketServer.class);

    ServerListener(ServerSocket serverSocket, ExecutionServiceInterface executionService, SocketWorkerFactoryInterface socketWorkerFactory) {
        this.serverSocket = serverSocket;
        this.executionService = executionService;
        this.socketWorkerFactory = socketWorkerFactory;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket accept = this.serverSocket.accept();
                this.executionService.execute(this.socketWorkerFactory.create(accept));
            } catch (IOException e) {
                logger.error("Error occurred while accepting connection.", e);
            }
        }
        try {
            this.serverSocket.close();
        } catch (IOException ignored) {
        }
    }
}
