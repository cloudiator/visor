package de.uniulm.omi.executionware.agent.monitoring.sensors.mysqlsensors;


import java.sql.ResultSet;
import java.sql.SQLException;

import de.uniulm.omi.executionware.agent.monitoring.api.Measurement;
import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;

/**
 * 
 * @author zarioha
 * A probe for measuring the MySQL metadata.
 * 
 */
public class NbFailedConnectionsMySQLSensor extends AbstractMySQLSensor 
{
	public NbFailedConnectionsMySQLSensor()
	{
		this.query =  makeRequest("Aborted_connects");
		preview = 0;
	}


    @Override
    protected Measurement getMeasurement(MonitorContext monitorContext) throws MeasurementNotAvailableException 
    {	
    	try {
			ResultSet rs = ps.executeQuery();
			long queryTimeMillis = System.currentTimeMillis();
			rs.next();			
			
			//TODO dont know if the better way is to send global value our per query value (not per second)
			int value = getPerQueryValue(rs.getInt("Value"));
			
			return new MeasurementImpl(queryTimeMillis,value);
		} catch (SQLException ex) {
			throw new MeasurementNotAvailableException("Error query execution");
		}  
    }
    
}