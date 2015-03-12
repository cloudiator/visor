package de.uniulm.omi.executionware.agent.monitoring.sensors.mysqlsensors;

import de.uniulm.omi.executionware.agent.monitoring.api.MySQLOptions;

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
		this.option = MySQLOptions.ABORTED_CONNECTS;
		preview = 0;
	}
    

    
    
}