package de.uniulm.omi.monitoring.server;

import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;

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

    protected final int port;
    protected final MetricReportingInterface metricReportingInterface;
    private ServerSocket serverSocket;
    private final ExecutorService executorService;

    private static final Logger logger = LogManager.getLogger(Server.class);

    public Server(int port, MetricReportingInterface metricReportingInterface) {
        this(port, metricReportingInterface, 1);
    }

    public Server(int port, MetricReportingInterface metricReportingInterface, int numOfWorkers) {
        this.port = port;
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
            throw new RuntimeException("Could not open socket on port " + port, e);
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
