package de.uniulm.omi.executionware.agent.monitoring.sensors.mysqlsensors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	protected PreparedStatement ps2 ;
	

    @Override
    protected void initialize() throws SensorInitializationException {   
    	super.initialize();
	    try {
	    	this.ps2=connection.prepareStatement("SHOW GLOBAL VARIABLES LIKE 'max_connections'");
	    } catch (SQLException ex) {
			throw new SensorInitializationException("Error prepared query");
		}
    }
    
	public PercentAllowedConnectionsMySQLSensor()
	{
		this.query =  makeRequest("Max_used_connections");
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
		} catch (SQLException ex) {
			throw new MeasurementNotAvailableException("Error query execution");
		}   
    }
}