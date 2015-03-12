package de.uniulm.omi.executionware.agent.monitoring.sensors.mysqlsensors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.uniulm.omi.executionware.agent.monitoring.api.Measurement;
import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.api.MySQLOptions;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;
import de.uniulm.omi.executionware.agent.monitoring.sensors.AbstractSensor;

/**
 * 
 * @author zarioha
 * A probe for measuring the MySQL metadata.
 * 
 */
public abstract class AbstractMySQLSensor extends AbstractSensor 
{
	private static Connection connection;

	//TODO by convention : use '%' user (anonymous) without password to read metadata
	private final String jdbcDriver = "org.drizzle.jdbc.DrizzleDriver";
	private final String jdbcName = "root";
	private final String jdbcPassword = "";	
	//TODO configure the URL by another way? (maybe by setMonitorContext())
	private final String jdbcUrl = "jdbc:drizzle://localhost:3306/";
	
	protected PreparedStatement ps ;
	protected MySQLOptions option;
	protected int preview;
	
	protected int getPerQueryValue(int val) {
		int value = val;
		int valuePerQuery = value-preview;
		preview = value;
		return valuePerQuery;
	}
	protected void prepareStatement(String query) throws SQLException {
		this.ps = connection.prepareStatement(query);
	} 

	
    @Override
    protected void initialize() throws SensorInitializationException {   
    	try  {
			Class.forName(jdbcDriver);
		} catch (Exception ex)  {
	        throw new SensorInitializationException("JdbcDriver not found");
		}
    	
		try {
			connection = DriverManager.getConnection(jdbcUrl,jdbcName,jdbcPassword);
		} catch (Exception ex) {
	        throw new SensorInitializationException("Error during connection");
		}

		try {
			String query =  makeRequest(option);
			prepareStatement(query);	
		} catch (SQLException ex) {
			throw new SensorInitializationException("Error prepared query");
		}
    }
    
    protected String makeRequest(MySQLOptions... vars) {
		// adding "/*!50002 GLOBAL */" for compatibility with all version of MySql
		// see documentation : http://dev.mysql.com/doc/refman/5.0/en/show-status.html
		String req = "SHOW /*!50002 GLOBAL */ STATUS";
		int i=0;
		for(MySQLOptions var : vars) {
			if(i==0) {
				req+=" where Variable_name like '"+var+"'";
				i++;
			}
			else {
				req+=" or Variable_name like '"+var+"'";
			}
		}
		return req;
	}
    
    

    @Override
    protected Measurement getMeasurement(MonitorContext monitorContext) throws MeasurementNotAvailableException {
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