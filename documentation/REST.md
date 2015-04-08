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
This action returns the currently active monitors/sensors within the system.
#### Example
```
[  
   {  
      "interval":{  
         "timeUnit":"SECONDS",
         "period":1
      },
      "contexts":[  
         {  
            "key":"localIp",
            "value":"134.60.146.196"
         }
      ],
      "sensorClassName":"de.uniulm.omi.cloudiator.visor.monitoring.sensors.CpuUsageSensor",
      "metricName":"cpu_usage",
      "links":[  
         {  
            "href":"/monitors/a5181ff0-dda1-45dd-8483-dfd604d0d38d",
            "rel":"self"
         }
      ]
   },
   {  
      "interval":{  
         "timeUnit":"SECONDS",
         "period":1
      },
      "contexts":[  
         {  
            "key":"localIp",
            "value":"134.60.146.196"
         }
      ],
      "sensorClassName":"de.uniulm.omi.cloudiator.visor.monitoring.sensors.MemoryUsageSensor",
      "metricName":"memory_usage",
      "links":[  
         {  
            "href":"/monitors/c7a98598-e64a-4f1f-8f28-054eb743ccf3",
            "rel":"self"
         }
      ]
   }
]
```
### GET /monitors/{uuid}
#### Description
This action returns the monitor registered under the given {uuid}.

#### Parameters
Parameter | Type    | Description
----------|-------- |-------------
uuid   | string | The identifier of the monitor. Mandatory.
#### Example
```
GET /monitors/c7a98598-e64a-4f1f-8f28-054eb743ccf3
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
  "sensorClassName":"de.uniulm.omi.cloudiator.visor.monitoring.sensors.MemoryUsageSensor",
  "links":[  
         {  
            "href":"/monitors/c7a98598-e64a-4f1f-8f28-054eb743ccf3",
            "rel":"self"
         }
      ]
}
```
### POST /monitors
#### Description
This action creates a new monitor. After successfull creating the new monitor will be returned. See GET.
#### Parameters
Parameter | Type    | Description
----------|-------- |-------------
contexts   | array[context] | An array of context objects. Each context object represents a specific monitor context which needs to be [supported by the sensors](Sensors.md). Each context object is a simple key => value object. Optional.
interval | interval | Represents the interval at which this sensor will be run. Is of type interval object. An interval has a period (number) and a timeUnit (String). Allowed values for the timeUnit attribute can be derived from the java [TimeUnit](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html) enumeration. Required.
metricName | String | The name for the metric. Required.
sensorClassName | String | The canonical name for the Java class that should be used as sensor. Must implement the Sensor Interface. Must be already present in the class path of visor. Required.
#### Example
```
POST /monitors
```
```
{  
  "contexts":[  
    {  
      "key":"pid",
      "value":50
    }
  ],
  "interval":{  
    "period":1,
    "timeUnit":"SECONDS"
  },
  "metricName":"memory_usage",
  "sensorClassName":"de.uniulm.omi.cloudiator.visor.monitoring.sensors.MemoryUsageSensor",
}
```

### PUT /monitors/{uuid}
#### Description
This action creates a new monitor. After successfull creation, the new monitor will be returned.

#### Parameters
Parameter | Type    | Description
----------|-------- |-------------
contexts   | array[context] | An array of context objects. Each context object represents a specific monitor context which needs to be [supported by the sensors](Sensors.md). Each context object is a simple key => value object. Optional.
interval | interval | Represents the interval at which this sensor will be run. Is of type interval object. An interval has a period (number) and a timeUnit (String). Allowed values for the timeUnit attribute can be derived from the java [TimeUnit](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html) enumeration. Required.
metricName | String | The name for the metric. Needs to be unique. Required.
sensorClassName | String | The canonical name for the Java class that should be used as sensor. Must implement the Sensor Interface. Must be already present in the class path of visor. Required.
uuid | String | Required. The identifier under which the monitor will be stored.
#### Example
```
PUT /monitors/c7a98598-e64a-4f1f-8f28-054eb743ccf3
```
```
{  
  "contexts":[  
    {  
      "key":"pid",
      "value":50
    }
  ],
  "interval":{  
    "period":1,
    "timeUnit":"SECONDS"
  },
  "metricName":"memory_usage",
  "sensorClassName":"de.uniulm.omi.cloudiator.visor.monitoring.sensors.MemoryUsageSensor",
}
```


### DELETE /monitors/{uuid}
#### Description
Stops the monitoring of the given {uuid}
#### Example
```
DELETE /monitors/c7a98598-e64a-4f1f-8f28-054eb743ccf3
```
