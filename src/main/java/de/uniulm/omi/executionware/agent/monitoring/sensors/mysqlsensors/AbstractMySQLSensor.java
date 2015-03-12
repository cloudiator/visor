package de.uniulm.omi.executionware.agent.monitoring.sensors.mysqlsensors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.sensors.AbstractSensor;

/**
 * 
 * @author zarioha
 * An abstract probe for measuring the MySQL metadata.
 * 
 */
public abstract class AbstractMySQLSensor extends AbstractSensor 
{
	//TODO have a single connection for all mysql sensor and close it when close monitoring
	protected static Connection connection;

	//TODO by convention : use '%' user (anonymous) without password to read metadata
	private final String jdbcDriver = "org.drizzle.jdbc.DrizzleDriver";
	private final String jdbcName = "root";
	private final String jdbcPassword = "";	
	//TODO configure the URL by another way? (maybe by setMonitorContext())
	private final String jdbcUrl = "jdbc:drizzle://localhost:3306/";
	
	protected PreparedStatement ps ;

	protected String query;
	protected int preview;

    //return the value added since the last value
	protected int getPerQueryValue(int val) {
		int value = val;
		int valuePerQuery = value-preview;
		preview = value;
		return valuePerQuery;
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
			this.ps = connection.prepareStatement(query);	
		} catch (SQLException ex) {
			throw new SensorInitializationException("Error prepared query");
		}
    }
    
    protected String makeRequest(String... vars) {
		// adding "/*!50002 GLOBAL */" for compatibility with all version of MySql
		// see documentation : http://dev.mysql.com/doc/refman/5.0/en/show-status.html
		String req = "SHOW /*!50002 GLOBAL */ STATUS";
		int i=0;
		for(String var : vars) {
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