package de.uniulm.omi.executionware.agent.monitoring.sensors.mysqlsensors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.uniulm.omi.executionware.agent.config.impl.FileConfigurationAccessor;
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
	private String jdbcDriver = "org.drizzle.jdbc.DrizzleDriver";
	private String jdbcName = "root";
	private String jdbcPassword = "";	
	//TODO configure the URL by another way? (maybe by setMonitorContext())
	private String jdbcUrl = "jdbc:drizzle://localhost:3306/";
	
	
	
    @Override
   // FileConfigurationAccessor
    protected void initialize() throws SensorInitializationException { 
		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException e) {
		    throw new SensorInitializationException("JdbcDriver not found",e);
		}
		try {
			connection = DriverManager.getConnection(jdbcUrl,jdbcName,jdbcPassword);
		} catch (SQLException e) {
			throw new SensorInitializationException("Error during connection",e);
		}
    }
}