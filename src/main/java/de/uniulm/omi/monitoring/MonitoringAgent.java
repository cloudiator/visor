package de.uniulm.omi.monitoring;

import de.uniulm.omi.monitoring.cli.CliOptions;
import de.uniulm.omi.monitoring.probes.impl.CpuUsageProbe;
import de.uniulm.omi.monitoring.probes.impl.MemoryUsageProbe;
import de.uniulm.omi.monitoring.reporting.api.MetricReportingInterface;
import de.uniulm.omi.monitoring.reporting.impl.KairosDb;
import de.uniulm.omi.monitoring.reporting.impl.MetricQueue;
import de.uniulm.omi.monitoring.scheduler.Scheduler;
import de.uniulm.omi.monitoring.server.Server;
import org.apache.commons.cli.*;

public class MonitoringAgent {

    public static void main(String[] args) throws ParseException {

        CliOptions.setArguments(args);

        //metric queue
        MetricReportingInterface metricQueue = new MetricQueue(2, new KairosDb(CliOptions.getKairosServer(), CliOptions.getKairosPort()));

        //create a new server
        Server server = new Server(9000, metricQueue);

        //run the server
        Thread thread = new Thread(server);
        thread.start();

        //create a scheduler
        Scheduler scheduler = new Scheduler(1, metricQueue);
        scheduler.registerProbe(new CpuUsageProbe());
        scheduler.registerProbe(new MemoryUsageProbe());
    }
}
