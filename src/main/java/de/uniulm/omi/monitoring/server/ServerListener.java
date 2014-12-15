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

package de.uniulm.omi.monitoring.server;

import de.uniulm.omi.monitoring.execution.api.ExecutionServiceInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by daniel on 15.12.14.
 */
public class ServerListener implements Runnable {

    private final ServerSocket serverSocket;
    private final ExecutionServiceInterface executionService;

    public ServerListener(ServerSocket serverSocket, ExecutionServiceInterface executionService) {
        this.serverSocket = serverSocket;
        this.executionService = executionService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket accept = this.serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
