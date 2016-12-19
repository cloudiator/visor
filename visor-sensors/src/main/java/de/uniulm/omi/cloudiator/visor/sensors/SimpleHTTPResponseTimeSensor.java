package de.uniulm.omi.cloudiator.visor.sensors;

import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.AbstractSensor;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;

/**
 * Created by frankgriesinger on 18.12.2016.
 *
 * A sensor for measuring response time of http service.
 */
public class SimpleHTTPResponseTimeSensor extends AbstractSensor {

    private String uri;

    @Override protected Measurement measureSingle() throws MeasurementNotAvailableException {

        URL url = null;
        try {
            url = new URL(this.uri);
        } catch (MalformedURLException e) {
            throw new MeasurementNotAvailableException("could not create URL: " + this.uri);
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            throw new MeasurementNotAvailableException("could not open connection");
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new MeasurementNotAvailableException("could not set GET method");
        }
        Date start = new Date();
        try {
            connection.connect();
        } catch (IOException e) {
            throw new MeasurementNotAvailableException("connection could not be established");
        }

        int code = 0;
        try {
            code = connection.getResponseCode();
        } catch (IOException e) {
            throw new MeasurementNotAvailableException("problem with receiving code");
        }

        if (code != 200){
            throw new MeasurementNotAvailableException("Response Code is " + code);
        }

        Date end = new Date();

        long responseTimeInMilliseconds = end.getTime() - start.getTime();


        return measurementBuilder(Long.class).now().value(responseTimeInMilliseconds).build();
    }

    @Override protected void initialize(MonitorContext monitorContext,
                                        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);

        Optional<String> uri = sensorConfiguration.getValue("uri");

        if(uri.isPresent()){
            this.uri = uri.get();
        } else {

            Optional<String> endpoint_ip = sensorConfiguration.getValue("endpoint_ip");
            Optional<String> path = sensorConfiguration.getValue("path");

            if(!endpoint_ip.isPresent() && !path.isPresent()) {
                this.uri = "http://127.0.0.1:8080";
            } else if(endpoint_ip.isPresent() && path.isPresent()){
                this.uri = "http://" + endpoint_ip + path.get();
            } else if(endpoint_ip.isPresent()){
                this.uri = "http://" + endpoint_ip;
            } else { // only path is present
                this.uri = "http://127.0.0.1" + path.get();
            }
        }
    }
}
