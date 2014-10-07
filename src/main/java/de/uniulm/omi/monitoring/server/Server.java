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

import de.uniulm.omi.monitoring.cli.CliOptions;
import de.uniulm.omi.monitoring.reporting.api.ReportingInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by daniel on 21.09.14.
 */
public class Server implements Runnable {

    protected final static int DEFAULT_PORT = 9002;
    protected final int port;
    protected final ReportingInterface metricReportingInterface;
    private ServerSocket serverSocket;
    private final ExecutorService executorService;

    private static final Logger logger = LogManager.getLogger(Server.class);

    public Server(ReportingInterface metricReportingInterface) {
        this(metricReportingInterface, 1);
    }
    public Server(ReportingInterface metricReportingInterface, int numOfWorkers) {

        if(CliOptions.getPort() == null) {
            this.port = DEFAULT_PORT;
        } else {
            this.port = CliOptions.getPort();
        }

        this.metricReportingInterface = metricReportingInterface;

        //create executor service
        this.executorService = Executors.newFixedThreadPool(numOfWorkers);
    }

    @Override
    public void run() {

        //open the server socket
        try {
            this.serverSocket = new ServerSocket(this.port);
            logger.info("Server started and is listening on port "+this.port);
        } catch (IOException e) {
            logger.fatal("Could not start server",e);
            System.exit(1);
        }


        while (true) {

            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                this.executorService.execute(new ServerWorker(clientSocket.getInputStream(), this.metricReportingInterface));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
