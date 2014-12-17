Building: the agent is build with maven.

```
usage: de.uniulm.omi.monitoring.MonitoringAgent
 -ip,--localIp <arg>      IP of the local machine
 -conf, --confFile <arg>  Location of the properties file.
```

A default configuration file can be found in the conf directory.

The monitoring agent starts a small telnet server on the port configured in the configuration file.
Communication with the port is line based:
applicationName metricName value timestamp\n

applicationName: the name of the application reporting the metric
metricName: the name of the metric you want to report
value: the value of the metric
timestamp: unix timestamp of the measurement

