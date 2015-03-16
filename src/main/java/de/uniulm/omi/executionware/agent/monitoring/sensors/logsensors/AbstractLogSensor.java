package de.uniulm.omi.executionware.agent.monitoring.sensors.logsensors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniulm.omi.executionware.agent.monitoring.api.Measurement;
import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;
import de.uniulm.omi.executionware.agent.monitoring.sensors.AbstractSensor;

/**
 * 
 * @author zarioha
 * This Sensor read log file from any ways with conditions
 */

public abstract class AbstractLogSensor extends AbstractSensor {

	private String fileName = "log.txt";
	private long filePointer;
	private RandomAccessFile file;

	protected List<String> contains  = new ArrayList<String>();
	protected List<String> dontContains  = new ArrayList<String>();
	protected Pattern requestPattern;
	
    protected void initialize() throws SensorInitializationException {
    	 try {
			file = new RandomAccessFile(fileName, "r");
		} catch (FileNotFoundException e) {
			throw new SensorInitializationException("Open File error for : "+fileName,e);
		}
    }
    
    @Override
    protected Measurement getMeasurement(MonitorContext monitorContext) throws MeasurementNotAvailableException {
		try 
		{
			//RandomAccessFile file = new RandomAccessFile(fileName, "r");
			List<String> lines =  new ArrayList<String>();
			if (file.length() < filePointer) 
			{
				String message = "Somes Lines Was ereased (chars from "+file.length()+" to "+filePointer+" are missing)";
				filePointer = 0;
				throw new MeasurementNotAvailableException(message);
			} 
			else 
			{
				file.seek(filePointer);
				String line;
				while ((line = file.readLine()) != null) 
				{
					//NOTE I do not understand these conditions
					boolean isOkLine = true;
					
					for(String temp:contains)
					{
						if (!line.contains(temp)) {	isOkLine = false; }
					}
					for(String temp:dontContains)
					{
						if (line.contains(temp)) { isOkLine = false; }
					}

					if(isOkLine)
					{
						if(requestPattern!=null)
						{
							Matcher requestMatcher = requestPattern.matcher(line);
							while (requestMatcher.find()) 
							{
								String temp = "";
								for (int i = 1; i <= requestMatcher.groupCount(); i++) {
									temp = temp + requestMatcher.group(i);
									if (i != requestMatcher.groupCount())
										temp = temp + ", ";
								}
								lines.add("{"+temp+"}");	
							}
						}
						else
						{
							lines.add(line);
						}	
					}
				}
				if (lines.size() > 0) 
				{
					//System.out.println(lines);
				}

				filePointer = file.getFilePointer();
				return new MeasurementImpl(System.currentTimeMillis(), lines);
			}

			//file.close();
		} catch (IOException e) {
			throw new MeasurementNotAvailableException("File access error for : "+ fileName,e);
		}
    }
}