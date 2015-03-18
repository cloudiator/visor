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
 * A probe for measuring the MySQL metadata : nb of query
 * 
 */
public class NbQueriesMySQLSensor extends AbstractMySQLSensor 
{    
	private int preview;
	private PreparedStatement ps ;

    //return the value added since the last value
	protected int getPerQueryValue(int val) {
		int value = val;
		int valuePerQuery = value-preview;
		preview = value;
		return valuePerQuery;
	}
	
    @Override
    protected void initialize() throws SensorInitializationException {   
    	super.initialize();

	    try {
			this.ps = connection.prepareStatement("SHOW /*!50002 GLOBAL */ STATUS where Variable_name like ?");
			ps.setString(1, "Queries");	
	    } catch (SQLException e) {
			throw new SensorInitializationException("Error prepared query",e);
		}
	    
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
		} catch (SQLException e) {
			throw new MeasurementNotAvailableException("Error query execution",e);
		}  
    }
}