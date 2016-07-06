package de.uniulm.omi.cloudiator.visor.sensors.cassandra;

import com.google.common.collect.ImmutableMap;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.*;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Daniel Seybold on 20.06.2016.
 */
public class CassandraSensor extends AbstractSensor {

    private static final String CASSANDRA_IP = "cassandra.ip";
    private static final String CASSANDRA_PORT = "cassandra.port";
    private final static String CASSANDRA_METRIC = "cassandra.metric";

    private Optional<String> cassandraIp;
    private Optional<String> cassandraPort;

    private JMXServiceURL cassandraMonitoringUrl;
    private JMXConnector jmxConnector;
    private MBeanServerConnection mBeanServerConnection;

    private Measureable metric;

    private static class Measurables implements Measureable {

        public static Measureable of(String string) {
            try {
                return RawMetric.valueOf(string);
            } catch (IllegalArgumentException ignored) {
            }

            /*
            //currently no composite metrics implemented
            try {
                return CompositeMetric.valueOf(string);
            } catch (IllegalArgumentException ignored) {

            }
            */

            throw new IllegalArgumentException(
                    String.format("Could not find metric with name %s", string));
        }

        @Override public Measurement measure(Map<RawMetric, Measurement> old,
                                             Map<RawMetric, Measurement> current) {
            return null;
        }
    }


    private enum RawMetric implements CassandraMetric, Measureable {
        TOTAL_DISK_SPACE_USED {

            @Override
            public String string() {
                return "org.apache.cassandra.metrics:type=ColumnFamily,name=TotalDiskSpaceUsed";
            }

            @Override
            public Object toType(String value) {
                return Long.valueOf(value);
            }

            @Override
            public String attribute() {
                return "Value";
            }

        },

        WRITE_THROUGHPUT_LATENCY {

            @Override
            public String string() {
                return "org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency";
            }

            @Override
            public Object toType(String value) {
               return Float.valueOf(value);
            }

            @Override
            public String attribute() {
                return "Mean";
            }

        },

        WRITE_REQUESTS{

            @Override
            public String string() {
                return "org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency";
            }

            @Override
            public Object toType(String value) {
                return Double.valueOf(value);
            }

            @Override
            public String attribute() {
                return "OneMinuteRate";
            }

        },

        READ_THROUGHPUT_LATENCY {

            @Override
            public String string() {
                return "org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency";
            }

            @Override
            public Object toType(String value) {
                return Double.valueOf(value);
            }

            @Override
            public String attribute() {
                return "Mean";
            }

        },

        READ_REQUESTS{

            @Override
            public String string() {
                return "org.apache.cassandra.metrics:type=ColumnFamily,name=ReadLatency";
            }

            @Override
            public Object toType(String value) {
                return Double.valueOf(value);
            }

            @Override
            public String attribute() {
                return "OneMinuteRate";
            }

        };



        @Override
        public Measurement measure(Map<RawMetric, Measurement> old, Map<RawMetric, Measurement> current) throws MeasurementNotAvailableException {
            return current.get(this);
        }
    }

    private interface CassandraMetric{
        String string();

        Object toType(String value);

        String attribute();


    }

    private interface Measureable {
        Measurement measure(Map<RawMetric, Measurement> old, Map<RawMetric, Measurement> current)
                throws MeasurementNotAvailableException;
    }

    @Override protected void initialize(MonitorContext monitorContext,
                                        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);

        this.cassandraIp = sensorConfiguration.getValue(CASSANDRA_IP);
        this.cassandraPort = sensorConfiguration.getValue(CASSANDRA_PORT);


        try {
            this.cassandraMonitoringUrl =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+this.cassandraIp.get()+":"+this.cassandraPort.get()+"/jmxrmi");
            this.jmxConnector = JMXConnectorFactory.connect(this.cassandraMonitoringUrl, null);
            this.mBeanServerConnection = this.jmxConnector.getMBeanServerConnection();

        } catch (MalformedURLException e) {
            throw new SensorInitializationException(
                    "Url provided for Cassandra  stats is malformed", e);
        }catch (IOException e){
            throw new SensorInitializationException("Unable to create MBean connection", e);
        }

        try {
            this.metric = Measurables.of(sensorConfiguration.getValue(CASSANDRA_METRIC).orElseThrow(
                    () -> new SensorInitializationException(
                            "Configuration parameter " + CASSANDRA_METRIC + " is required")));
        } catch (IllegalArgumentException e) {
            throw new SensorInitializationException(e);
        }


    }

    @Override
    protected Measurement measure() throws MeasurementNotAvailableException {

        return metric.measure(new RawMetricSupplier(this.mBeanServerConnection).get(),new RawMetricSupplier(this.mBeanServerConnection).get());


    }

    private interface MetricSupplier<T>{

        T get() throws MeasurementNotAvailableException;
    }

    private static class RawMetricSupplier implements MetricSupplier<Map<RawMetric, Measurement>>{

        private final MBeanServerConnection mBeanServerConnection;

        private RawMetricSupplier(MBeanServerConnection mBeanServerConnection){
            this.mBeanServerConnection = mBeanServerConnection;
        }

        @Override
        public Map<RawMetric, Measurement> get() throws MeasurementNotAvailableException {
            Map<RawMetric, Measurement> measurements = new HashMap<>(RawMetric.values().length);
            try {

                for (RawMetric rawMetric : RawMetric.values()) {

                    ObjectName mbeanObjectName = new ObjectName(rawMetric.string());
                    String attribute = rawMetric.attribute();
                    String mbeanValue =  this.mBeanServerConnection.getAttribute(mbeanObjectName, attribute).toString();


                    measurements.put(rawMetric, MeasurementBuilder.newBuilder().now().value(rawMetric.toType(mbeanValue)).build());
                }



            } catch (IOException | MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException |ReflectionException | MBeanException e) {

                throw new MeasurementNotAvailableException(e);

            }
            return ImmutableMap.copyOf(measurements);

        }
    }
}
