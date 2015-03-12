package de.uniulm.omi.executionware.agent.monitoring.sensors;

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

/**
 * 
 * @author zarioha
 * A probe for measuring the MySQL metadata.
 * 
 */
public class MySQLSensor extends AbstractSensor 
{
	private static Connection connection;

	//TODO by convention : use '%' user (anonymous) without password to read metadata
	private final String jdbcDriver = "org.drizzle.jdbc.DrizzleDriver";
	private final String jdbcName = "root";
	private final String jdbcPassword = "";
	
	//TODO configure the URL by another way? (maybe by setMonitorContext())
	private final String jdbcUrl = "jdbc:drizzle://localhost:3306/";
	
	private PreparedStatement ps ;

    @Override
    protected Measurement getMeasurement(MonitorContext monitorContext) throws MeasurementNotAvailableException {
    	try {
			ResultSet rs = ps.executeQuery();
			long queryTimeMillis = System.currentTimeMillis();
			List <Measurement> mesurements = new ArrayList(); 
			while(rs.next()) 
			{
				String variableName = rs.getString("Variable_name");
				String value = rs.getString("Value");
				mesurements.add(new MeasurementImpl(queryTimeMillis,value));
			}
			Measurement m;
			return mesurements.get(0);
		} catch (SQLException ex) {
			throw new MeasurementNotAvailableException("Error query execution");
		}   
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
			String query =  makeRequest(MySQLOptions.QUERIES/*,MySQLOptions.QUESTIONS*/);
			ps = connection.prepareStatement(query);	
		} catch (SQLException ex) {
			throw new SensorInitializationException("Error prepared query");
		}
    } 
    
	private String makeRequest(MySQLOptions... vars) {
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
}