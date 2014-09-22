package de.uniulm.omi.monitoring;

import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;
import de.uniulm.omi.monitoring.reporting.impl.KairosDb;
import de.uniulm.omi.monitoring.reporting.impl.MetricQueue;
import de.uniulm.omi.monitoring.server.Server;

public class MonitoringAgent
{

    public static String localIp;
    public static String applicationName;

    public static void main( String[] args )
    {

        //handle the arguments
        // 1. LocalServer IP
        // 2. KairosServer IP
        // 3. KairosServer Port

        if(args.length != 3) {
            System.exit(1);
        }

        localIp = args[0];
        String kairosServer = args[1];
        String kairosServerPort = args[2];

        //initialize a new metric queue
        MetricReportingInterface metricReportingInterface = new MetricQueue(2, new KairosDb(kairosServer, kairosServerPort));

        //create a new server
        Server server = new Server(9000,metricReportingInterface);

        //run the server
        Thread thread = new Thread(server);
        thread.start();
    }
}
