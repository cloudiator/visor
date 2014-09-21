package de.uniulm.omi.monitoring;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Metric Agent started" );

        //initialize a new metric queue
        MetricReportingInterface metricReportingInterface = new MetricQueue(1, new KairoReporting());

        //create a new server
        Server server = new Server(1000,metricReportingInterface);

        //run the server
        Thread thread = new Thread(server);
        thread.start();

    }
}
