package de.uniulm.omi.monitoring;

import de.uniulm.omi.monitoring.probes.RandomProbe;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;
import de.uniulm.omi.monitoring.reporting.impl.KairosDb;
import de.uniulm.omi.monitoring.reporting.impl.MetricQueue;
import de.uniulm.omi.monitoring.scheduler.Scheduler;
import de.uniulm.omi.monitoring.server.Server;

public class MonitoringAgent
{

    public static String localIp;

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

        //metric queue
        MetricReportingInterface metricQueue = new MetricQueue(2, new KairosDb(kairosServer, kairosServerPort));

        //create a new server
        Server server = new Server(9000,metricQueue);

        //run the server
        Thread thread = new Thread(server);
        thread.start();

        //create a scheduler
        Scheduler scheduler = new Scheduler(1,metricQueue);
        scheduler.registerProbe(new RandomProbe("random1"));
        scheduler.registerProbe(new RandomProbe("random2"));
        scheduler.registerProbe(new RandomProbe("random3"));
    }
}
