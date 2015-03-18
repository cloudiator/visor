
package de.uniulm.omi.executionware.agent.monitoring.sensors.logsensors;

import java.util.regex.Pattern;

import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.sensors.AbstractSensor;

/**
 * 
 * @author zarioha
 * This Sensor read log file from OFBizLog
 */

public class OFBizLogSensor extends AbstractLogSensor {

	public OFBizLogSensor() {
		this.fileName = "logs/ofbiz.log";
	}
	
	public static void main(String[] args) throws SensorInitializationException, MeasurementNotAvailableException {
		AbstractSensor logReader = new OFBizLogSensor();
		logReader.init();
		logReader.getMeasurement();
	}

    protected void initialize() throws SensorInitializationException {
    	super.initialize();
    	String pattern = "(19|20\\d{2})-(0?[1-9]|1[012])-(0?[1-9]|[12]\\d|3[01]) ([01]?\\d|2[0-3]):([0-5]\\d):([0-5]\\d),(\\d{3}).*\\((http.+?)\\).*\\[\\[\\[(.+?)\\(Domain.*(Request Done).*total:(.*),";
    	this.requestPattern = Pattern.compile(pattern);
    
    }
}