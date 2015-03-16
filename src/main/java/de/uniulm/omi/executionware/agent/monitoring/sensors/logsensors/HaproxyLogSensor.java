
package de.uniulm.omi.executionware.agent.monitoring.sensors.logsensors;

import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.sensors.AbstractSensor;

/**
 * 
 * @author zarioha
 * This Sensor read log file from Haproxy
 */

public class HaproxyLogSensor extends AbstractLogSensor {

	public static void main(String[] args) throws SensorInitializationException, MeasurementNotAvailableException {
		AbstractSensor logReader = new HaproxyLogSensor();
		logReader.init();
		logReader.getMeasurement();
	}

    protected void initialize() throws SensorInitializationException {
    	super.initialize();
    	 
    	this.contains.add("JSESSIONID");
    	this.dontContains.add(".css");
    }
}