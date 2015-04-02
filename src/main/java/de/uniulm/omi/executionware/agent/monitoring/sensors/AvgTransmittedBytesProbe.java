package de.uniulm.omi.executionware.agent.monitoring.sensors;

import java.util.ArrayList;

import org.hyperic.sigar.NetInterfaceStat;
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

public class AvgTransmittedBytesProbe implements Sensor{

	private static int SMALL_CYCLE= 1000, BIG_CYCLE= 5000;
	Sigar sigarImpl;
	SigarProxy sigar;

	/**
	 * Average rate of transmitted bytes in bytes per second. Blocking method, execute it in a separate thread
	 * @return
	 * @throws SigarException
	 * @throws InterruptedException
	 */
	public double getAverageTxRate() throws SigarException, InterruptedException
	{
		int smallCycle = SMALL_CYCLE;
		int bigCycle = BIG_CYCLE;
		NetInterfaceStat netStat = null;
		long txBytesLastCycle = 0;
		long txBytesNewCycle = 0;
		ArrayList<Long> txBytesTotal = new ArrayList<Long>();
		long averageBytesPerSecond; 
		
		// measure transmitted bytes during bigCycle period
		for(int i=0;i<=bigCycle; i+=smallCycle)
		{
			txBytesNewCycle = 0;
			this.sigar=SigarProxyCache.newInstance(sigarImpl);
			
			// measure the number of received bytes on all interfaces
			for(String ni : sigar.getNetInterfaceList())
			{
				netStat = this.sigar.getNetInterfaceStat(ni);
				if(i==0)
					txBytesLastCycle+=netStat.getTxBytes();
				else
					txBytesNewCycle+=netStat.getTxBytes();
			}
			// we are interested on the changed values
			if(txBytesNewCycle - txBytesLastCycle > 0)
			{
				txBytesTotal.add(txBytesNewCycle-txBytesLastCycle);
				txBytesLastCycle = txBytesNewCycle;
			}
				
			Thread.sleep(smallCycle);
		}
		// get average value for all non zero measurements
		averageBytesPerSecond = this.calculateAverageRate(txBytesTotal);
		
		return averageBytesPerSecond;
	}
	//TODO: substitute by some more beautiful calculation
	private long calculateAverageRate(ArrayList<Long> measurements){
		long result=0;
		for(Long m : measurements)
			result += m;
		if(result!=0)
			result = result/measurements.size();
		return result;
	}
	
	public AvgTransmittedBytesProbe(){
		this.sigarImpl = new Sigar();
		
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
		//"kBytes/sec"
		double averageTxRate = 0;
		try {
			averageTxRate = getAverageTxRate()/ 1024 ;
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new MeasurementImpl(System.currentTimeMillis(), averageTxRate);
	}

}
