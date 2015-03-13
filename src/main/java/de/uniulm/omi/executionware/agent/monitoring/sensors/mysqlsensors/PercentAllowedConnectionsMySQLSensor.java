package de.uniulm.omi.executionware.agent.monitoring.sensors.mysqlsensors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.uniulm.omi.executionware.agent.config.impl.FileConfigurationAccessor;
import de.uniulm.omi.executionware.agent.monitoring.api.Measurement;
import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;

/**
 * 
 * @author zarioha
 * A probe for measuring the MySQL metadata : percent of used connections
 * 
 */
public class PercentAllowedConnectionsMySQLSensor extends AbstractMySQLSensor 
{    

	private PreparedStatement ps;
	private PreparedStatement ps2;
	
    @Override
    protected void initialize() throws SensorInitializationException {   
    	super.initialize();

	    try {
			this.ps = connection.prepareStatement("SHOW /*!50002 GLOBAL */ STATUS where Variable_name like ?");
			ps.setString(1, "Max_used_connections");	
	    	this.ps2= connection.prepareStatement("SHOW GLOBAL VARIABLES LIKE ?");
			ps2.setString(1, "max_connections");
	    } catch (SQLException e) {
			throw new SensorInitializationException("Error prepared query",e);
		}
    }
    
    protected Measurement getMeasurement(MonitorContext monitorContext) throws MeasurementNotAvailableException {
    	try {			
			ResultSet rs = ps.executeQuery();
			ResultSet rs2 = ps2.executeQuery();
			long queryTimeMillis = System.currentTimeMillis();
			
			rs.next();			
			int value = rs.getInt("Value");
		    rs2.next();
		    int value2 = rs2.getInt("Value"); 
		    
			return new MeasurementImpl(queryTimeMillis,value*100./value2);
		} catch (SQLException e) {
			throw new MeasurementNotAvailableException("Error query execution",e);
		}   
    }
}