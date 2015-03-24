# Visor - a simple monitoring agent for the cloudiator toolchain.
***
## Description

Visor is a simple monitoring agent used within the cloudiator toolchain. It is responsible
for monitoring servers/virtual machines. It supports multiple sensor for gathering resource information. In addition it offers a telnet interface, allowing applications on the same machine to report metrics to the monitoring infrastructure.

The collected data will be stored within a configurable time-series database.

For configuration purposes, it offers a small RESTful interface, allowing the start/stop spoecific sensors.

***
## Building
The agent is build using maven:
```
mvn clean install
```
Afterwards a bundled jar with dependencies can be found in the target folder.
***
## Usage
Visor requires JRE 8.
```
java -jar target/visor-{version}-jar-with-dependencies.jar
```
```
usage: de.uniulm.omi.cloudiator.visor.MonitoringAgent
 -conf,--configFile <arg>   Configuration file location.
 -ip,--localIp              IP of the local machine (optional)
```
***
## Configuration
A default configuration file can be found in the conf directory.
***
## Documentation
[See Documentation for more details.](documentation/README.md)

