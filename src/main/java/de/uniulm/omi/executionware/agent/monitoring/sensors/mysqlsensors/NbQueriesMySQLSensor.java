package de.uniulm.omi.executionware.agent.monitoring.sensors.mysqlsensors;

import de.uniulm.omi.executionware.agent.monitoring.api.MySQLOptions;

/**
 * 
 * @author zarioha
 * A probe for measuring the MySQL metadata.
 * 
 */
public class NbQueriesMySQLSensor extends AbstractMySQLSensor 
{    

	public NbQueriesMySQLSensor()
	{
		this.option = MySQLOptions.QUERIES;
	}
    
}