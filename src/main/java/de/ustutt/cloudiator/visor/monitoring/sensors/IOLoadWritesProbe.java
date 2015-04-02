package de.ustutt.cloudiator.visor.monitoring.sensors;

import java.lang.management.ManagementFactory;

import org.hyperic.sigar.DiskUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

import com.google.common.base.Optional;
import com.sun.management.OperatingSystemMXBean;

import de.uniulm.omi.executionware.agent.monitoring.api.InvalidMonitorContextException;
import de.uniulm.omi.executionware.agent.monitoring.api.Measurement;
import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.api.Sensor;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;

public class IOLoadWritesProbe implements Sensor{

	Sigar sigarImpl;
	SigarProxy sigar;
	
	public IOLoadWritesProbe(){
		this.sigarImpl = new Sigar();
		this.sigar=SigarProxyCache.newInstance(sigarImpl);
	}
	
	public String outputDisk() throws SigarException {
		 OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		 String rootOS = osBean.getName().indexOf("win") >= 0 ? MonitorContext.FS_ROOT_WINDOWS:MonitorContext.FS_ROOT_LINUX; 
		 DiskUsage disk = sigar.getDiskUsage(rootOS); 
		 //return " Writes-bytes in Disk: " + Sigar.formatSize(disk.getWriteBytes()) + " | Writes: " + disk.getWrites();
		 return "Writes in Disk: " + disk.getWrites();
	}
	
	@Override
	public void init() throws SensorInitializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMonitorContext(Optional<MonitorContext> monitorContext)
			throws InvalidMonitorContextException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Measurement getMeasurement() throws MeasurementNotAvailableException {
		String diskIO = "";
		try {			
			diskIO = outputDisk();			
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (diskIO.equals(null)) {
	        throw new MeasurementNotAvailableException("IODisk calculation wasn´t possible!");
	    }
		return new MeasurementImpl(System.currentTimeMillis(), diskIO);
	}

}
