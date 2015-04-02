package de.ustutt.cloudiator.visor.monitoring.sensors;

import java.io.File;
import java.io.IOException;

import org.hyperic.sigar.NfsClientV3;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

import com.google.common.base.Optional;

import de.uniulm.omi.executionware.agent.monitoring.api.InvalidMonitorContextException;
import de.uniulm.omi.executionware.agent.monitoring.api.Measurement;
import de.uniulm.omi.executionware.agent.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.executionware.agent.monitoring.api.Sensor;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;

public class NFSV3Probe implements Sensor{
	Sigar sigarImpl;
	SigarProxy sigar;
	
	public NFSV3Probe(){
		this.sigarImpl = new Sigar();
	}
	
	/**
	 * Check if nfs is available, e.g., mounted on the system.
	 * Make sure that the method is allowed to write to nfs folder.
	 * It is writing and deleting the file, measures the number of create operations to nfs.
	 * If the number is increased, NFS is available, if not, it is offline.
	 * Only shares accessed with NFSv3 protocol are supported by this method
	 * @param nfsMountPoint
	 * @return
	 * @throws SigarException
	 * @throws IOException
	 */
	public boolean isNFSV3Available() throws SigarException, IOException{
		this.sigar = SigarProxyCache.newInstance(sigarImpl);
		long numberOfCreates = 0;
		try{
			NfsClientV3 nfsClient = sigar.getNfsClientV3();
			nfsClient = sigar.getNfsClientV3();
			numberOfCreates = nfsClient.getCreate();
			// file name should not conflict with any of already existing files in nfs directory
			File file = new File(MonitorContext.NFS_MOUNT_POINT+MonitorContext.FILE_NAME_TEST);
			file.createNewFile();
			file.delete();
			this.sigar = SigarProxyCache.newInstance(sigarImpl);
			nfsClient = sigar.getNfsClientV3();
			if(nfsClient.getCreate() > numberOfCreates)
				return true;
			else
				return false;	
		}
		// sigar throws an exception if nfs was not mounted
		catch(SigarException e){
			return false;
		}
		
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
		boolean isNFS3 = false;
		try {
			isNFS3 = isNFSV3Available();
		} catch (IOException | SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!isNFS3) {
	        throw new MeasurementNotAvailableException("NFS V3 is not available");
	    }
		return new MeasurementImpl(System.currentTimeMillis(), isNFS3);
	}

}
