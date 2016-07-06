# Sensors
***
## CPU Sensor
### ClassName
de.uniulm.omi.cloudiator.visor.sensors.SystemCpuUsageSensor
### Description
The CPU sensor uses a [com.sun.management.OperatingSystemMXBean](https://docs.oracle.com/javase/8/docs/jre/api/management/extension/com/sun/management/OperatingSystemMXBean.html)to measure the CPU load of the underlying system. It reports the CPU usage in % at time t.
### Configuration
No configuration is needed.
***
## Memory Sensor
### ClassName
de.uniulm.omi.cloudiator.visor.sensors.SystemMemoryUsageSensor
### Description
The memory sensor uses a [com.sun.management.OperatingSystemMXBean](https://docs.oracle.com/javase/8/docs/jre/api/management/extension/com/sun/management/OperatingSystemMXBean.html) to measure the current RAM load on the system. It reports the memory usage in % at time t.
### Configuration
No configuration is needed.
***
## Network Sensor
***
## File System Usage Sensor
***
## Apache Status Sensor
***
## HA_Proxy Sensor
***
## Cassandra Sensor
### ClassName
de.uniulm.omi.cloudiator.visor.sensors.cassandra.CassandraSensor
### Description
The Cassandra sensor measures [Cassandra specific metrics](https://wiki.apache.org/cassandra/Metrics). 

Possible metric types: 
- TOTAL_DISK_SPACE_USED (in bytes)
- WRITE_THROUGHPUT_LATENCY 
- WRITE_REQUESTS
- READ_THROUGHPUT_LATENCY 
- READ_REQUESTS

### Sensor Configuration
```json
"sensorConfiguration":{
        "cassandra.ip":"1.2.3.4",
        "cassandra.port":"7199",
      	"cassandra.metric": "TOTAL_DISK_SPACE_USED"
    }
```
