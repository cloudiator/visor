# /monitors

## GET /monitors
### Description
This action returns the currently active monitors/sensors within the system.
### Example
```
[  
   {  
      "interval":{  
         "timeUnit":"SECONDS",
         "period":1
      },
      monitorContext: {
         "local.ip": "134.60.30.150"
      },
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
      monitorContext: {
          "local.ip": "134.60.30.150"
      },
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
## GET /monitors/{uuid}
### Description
This action returns the monitor registered under the given {uuid}.

### Parameters
Parameter | Type    | Description
----------|-------- |-------------
uuid   | string | The identifier of the monitor. Mandatory.
### Example
```
GET /monitors/c7a98598-e64a-4f1f-8f28-054eb743ccf3
```
```
{  
  monitorContext: {
    "local.ip": "134.60.30.150"
  },
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
## POST /monitors
### Description
This action creates a new monitor. After successful creating the new monitor will be returned. See GET.
### Parameters
Parameter | Type    | Description
----------|-------- |-------------
monitorContext   | map | A map of key-value objects. Each key-value object represents a specific monitor context which needs to be [supported by the sensors](Sensors.md). Optional.
interval | interval | Represents the interval at which this sensor will be run. Is of type interval object. An interval has a period (number) and a timeUnit (String). Allowed values for the timeUnit attribute can be derived from the java [TimeUnit](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html) enumeration. Required.
metricName | String | The name for the metric. Required.
sensorClassName | String | The canonical name for the Java class that should be used as sensor. Must implement the Sensor Interface. Must be already present in the class path of visor. Required.
### Example
```
POST /monitors
```
```
{  
  monitorContext: {
    "local.ip": "134.60.30.150"
  },
  "interval":{  
    "period":1,
    "timeUnit":"SECONDS"
  },
  "metricName":"memory_usage",
  "sensorClassName":"de.uniulm.omi.cloudiator.visor.sensors.MemoryUsageSensor"
}
```

## PUT /monitors/{uuid}
### Description
This action creates a new monitor. After successful creation, the new monitor will be returned.

### Parameters
Parameter | Type    | Description
----------|-------- |-------------
monitorContext   | map| A map of key-value objects. Each key-value object represents a specific monitor context which needs to be [supported by the sensors](Sensors.md). Optional.
interval | interval | Represents the interval at which this sensor will be run. Is of type interval object. An interval has a period (number) and a timeUnit (String). Allowed values for the timeUnit attribute can be derived from the java [TimeUnit](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html) enumeration. Required.
metricName | String | The name for the metric. Needs to be unique. Required.
sensorClassName | String | The canonical name for the Java class that should be used as sensor. Must implement the Sensor Interface. Must be already present in the class path of visor. Required.
uuid | String | Required. The identifier under which the monitor will be stored.
### Example
```
PUT /monitors/c7a98598-e64a-4f1f-8f28-054eb743ccf3
```
```
{  
  monitorContext: {
    "local.ip": "134.60.30.150"
  },
  "interval":{  
    "period":1,
    "timeUnit":"SECONDS"
  },
  "metricName":"memory_usage",
  "sensorClassName":"de.uniulm.omi.cloudiator.visor.sensors.MemoryUsageSensor"
}
```


## DELETE /monitors/{uuid}
### Description
Stops the monitoring of the given {uuid}
### Example
```
DELETE /monitors/c7a98598-e64a-4f1f-8f28-054eb743ccf3
```

## DELETE /monitors/
### Description 
Stops all currently running monitors.
### Example
```
DELETE /monitors/
```
