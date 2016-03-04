# Telnet Interface
***
## Description
***
## Deployment
A new telnet server can be started using the RESTful interface. See [Servers](rest/Servers.md).
***
## Usage
The communication with the telnet interface is line based, where a line represents exactly one metric:
```
metricName value timestamp\n
```
Value           | Description
--------------- | -----------
metricName      | The name of the metric that is reported.
value           | The value of the metric.
timestamp       | The unix timestamp when this metric value occured.

The TCP connection currently has a fixed timeout of 20 seconds.

## Clients
Currently a java and a C# client are under development.
