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

import de.uniulm.omi.monitoring.cli.CliOptions;
import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;
import de.uniulm.omi.monitoring.server.api.RequestParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A small server.
 * <p/>
 * Receives requests on the defined port, parses them with
 * the defined request parses, and then reports the result
 * to the given reporting interface.
 */
public class Server<T> implements Runnable {



    protected final int port;
    protected final ReportingInterface<T> reportingInterface;
    protected final RequestParser<T> requestParser;
    private ServerSocket serverSocket;
    private final ExecutorService executorService;

    private static final Logger logger = LogManager.getLogger(Server.class);

    public Server(int port, ReportingInterface<T> reportingInterface, RequestParser<T> requestParser, int numberOfWorkers) {
        this.port = port;
        this.reportingInterface = reportingInterface;
        this.requestParser = requestParser;
        this.executorService = Executors.newFixedThreadPool(numberOfWorkers);
    }

    @Override
    public void run() {

        //open the server socket
        try {
            this.serverSocket = new ServerSocket(this.port);
            logger.info("Server started and is listening on port " + this.port);
        } catch (IOException e) {
            logger.fatal("Could not start server", e);
            System.exit(1);
        }


        while (true) {
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
                this.executorService.execute(new ServerWorker<>(clientSocket.getInputStream(), this.requestParser, this.reportingInterface));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
