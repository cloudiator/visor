# RESTful Interface
***
## Description
The RESTful interface can be used to start the usage of sensors at the targeted visor agent. It also allows to reconfigure sensors and to stop them.
## Configuration
The port of the rest server can be configured using the "restPort" configuration property of the configuration file.
***
## Usage
### GET /monitors
#### Description
This action returns the currently active monitors/sensors which are active within the system.
#### Example
```
[  
  {  
    "contexts":[  
      {  
        "key":"localIp",
        "value":"134.60.30.150"
      }
    ],
    "interval":{  
      "period":1,
      "timeUnit":"SECONDS"
    },
    "metricName":"memory_usage",
    "sensorClassName":"de.uniulm.omi.cloudiator.visor.monitoring.sensors.MemoryUsageSensor"
  },
  {  
    "contexts":[  
      {  
        "key":"localIp",
        "value":"134.60.30.150"
      }
    ],
    "interval":{  
      "period":1,
      "timeUnit":"SECONDS"
    },
    "metricName":"cpu_usage",
    "sensorClassName":"de.uniulm.omi.cloudiator.visor.monitoring.sensors.CpuUsageSensor"
  }
]
```
### GET /monitors/{metricName}
#### Description
This action returns the monitor registered under the given {metricName}.
#### Example
```
GET /monitors/memory_usage
```
```
{  
  "contexts":[  
    {  
      "key":"localIp",
      "value":"134.60.30.150"
    }
  ],
  "interval":{  
    "period":1,
    "timeUnit":"SECONDS"
  },
  "metricName":"memory_usage",
  "sensorClassName":"de.uniulm.omi.cloudiator.visor.monitoring.sensors.MemoryUsageSensor"
}
```
### POST /monitors
#### Description
This action creates a new monitor.
#### Parameters
Parameter | Type    | Description
----------|-------- |-------------
contexts   | array[context] | An array of context objects. Each context object represents a specific monitor context which needs to be [supported by the sensors](Sensors.md). Each context object is a simple key => value object. Optional.
interval | interval | Represents the interval at which this sensor will be run. Is of type interval object. An interval has a period (number) and a timeUnit (String). Allowed values for the timeUnit attribute can be derived from the java [TimeUnit](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html) enumeration. Required.
metricName | String | The name for the metric. Needs to be unique. Required.
sensorClassName | String | The canonical name for the Java class that should be used as sensor. Must implement the Sensor Interface. Must be already present in the class path of visor. Required.
#### Example
```
POST /monitors/memory_usage
```
```
{  
  "interval":{  
    "period":1,
    "timeUnit":"SECONDS"
  },
  "metricName":"memory_usage",
  "sensorClassName":"de.uniulm.omi.cloudiator.visor.monitoring.sensors.MemoryUsageSensor"
}
```
### DELETE /monitors/{metricName}
#### Description
Stops the monitoring of the given {metricName}
#### Example
```
DELETE /monitors/memory_usage
```