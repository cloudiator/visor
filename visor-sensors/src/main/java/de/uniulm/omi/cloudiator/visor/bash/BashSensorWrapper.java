package de.uniulm.omi.cloudiator.visor.bash;

import java.io.File;

import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.AbstractSensor;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;

/**
 * A probe for calling bash scripts that do the actual measuring.
 */
public class BashSensorWrapper extends AbstractSensor {

    private static final String FILE_NAME_KEY = "sensor.bash.filename";
	
	private File scriptFile;
	
	@Override protected void initialize(MonitorContext monitorContext,
			SensorConfiguration sensorConfiguration) throws SensorInitializationException {
		
		String osName = System.getProperty("os.name");
		if(!isLinux(osName)) {
			throw new SensorInitializationException("BashSensorWrapper only available for Linux based systems, but found " + osName);
		}
		super.initialize(monitorContext, sensorConfiguration);
	        
	}
	
	@Override protected Measurement measure() throws MeasurementNotAvailableException {
		
	}
	
	private static boolean isWindows(String os) {
		return (os.indexOf("win") >= 0);
	}

	private static boolean isMac(String os) {
		return (os.indexOf("mac") >= 0);
	}

	private static boolean isUnix(String os) {
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 );
	}

	private static boolean isSolaris(String os) {
		return (os.indexOf("sunos") >= 0);
	}
	
	private static boolean isLinux(String osName) throws SensorInitializationException {
		if(osName == null || isMac(osName) || isWindows(osName) || isSolaris(osName))
			return false;
		if(!isUnix(osName)){
			throw new SensorInitializationException("found unsupported OS, running Mythos?: " + osName);
		}
		
		// String osArch = System.getProperty("os.arch");
		// String osVersion = System.getProperty("os.version");
		
		return true;
	}
}
