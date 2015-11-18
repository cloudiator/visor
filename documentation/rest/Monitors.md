# /monitors

## GET /monitors
### Description
This action returns the currently active monitors/sensors within the system.

## GET /monitors/{uuid}
### Description
This action returns the monitor registered under the given {uuid}.

### Parameters
Parameter | Type    | Description
----------|-------- |-------------
uuid   | string | The identifier of the monitor. Mandatory.

## POST /monitors
### Description
This action creates a new monitor. After successful creating the new monitor will be returned. See GET.

## PUT /monitors/{uuid}
### Description
This action creates a new monitor. After successful creation, the new monitor will be returned.

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

## Possible Monitors



### PushMonitor

A monitor for monitoring push metrics send by the application to the telnet interface.

#### Parameters

Parameter | Type | Description
----------|------| -----------
@type | String | The type of the monitor. push for a push monitor
metricName | String | The name of the metric (mandatory)
componentId | String | A unique identifier for the component instance (mandatory)
monitorContext | map | A key-value map used as context for the monitor (optional)
port | int | port of the started server (only on response)

#### Example
##### Request
```
{
    "@type":"push",
    "metricName":"MyMetricName",
    "componentId":"d1d63337-51bb-497d-8752-8d242974bd56",
    "monitorContext":{
        "local.ip":"134.60.241.111",
        "os.name":"Mac OS X",
        "java.version":"1.8.0_40",
        "os.arch":"x86_64",
        "os.version":"10.11.1"
    }
}
```
##### Response
```
{
    "entity":{
        "@type":"push",
        "metricName":"MyMetricName",
        "componentId":"d1d63337-51bb-497d-8752-8d242974bd56",
        "monitorContext":{
            "local.ip":"134.60.241.111",
            "os.name":"Mac OS X",
            "java.version":"1.8.0_40",
            "os.arch":"x86_64",
            "os.version":"10.11.1"
        },
        "port":49154
    },
    "links":[
        {
            "href":"/monitors/924db1bd-464f-4f41-bf0c-66b55b3abfc4",
            "rel":"self"
        }
    ]
}
```


### SensorMonitor

Sensor monitor is a monitor that uses a sensor class to measure data on the virtual machine.

#### Parameters

Parameter | Type | Description
----------|------| -----------
@type | String | The type of the monitor. push for a push monitor
metricName | String | The name of the metric (mandatory)
componentId | String | A unique identifier for the component instance (mandatory)
monitorContext | map | A key-value map used as context for the monitor (optional)
sensorClassName | String | The class name of the sensor
interval | Object | period: the period for sampling, timeUnit: the TimeUnit of sampling

#### Example
##### Request
```
{
    "@type":"sensor",
    "metricName":"MyMetricName",
    "componentId":"d1d63337-51bb-497d-8752-8d242974bd56",
    "sensorClassName":"de.uniulm.omi.cloudiator.visor.sensors.CpuUsageSensor",
    "interval":{
        "period":1,
        "timeUnit":"SECONDS"
    }
}
```
##### Response
```
{
    "entity":{
        "@type":"sensor",
        "metricName":"MyMetricName",
        "componentId":"d1d63337-51bb-497d-8752-8d242974bd56",
        "monitorContext":{
            "local.ip":"128.93.160.239",
            "os.name":"Mac OS X",
            "java.version":"1.8.0_40",
            "os.arch":"x86_64",
            "os.version":"10.11.1"
        },
        "sensorClassName":"de.uniulm.omi.cloudiator.visor.sensors.CpuUsageSensor",
        "interval":{
            "period":1,
            "timeUnit":"SECONDS"
        }
    },
    "links":[
        {
            "href":"/monitors/b1af8355-2b7b-4902-a60f-034c5edda37a",
            "rel":"self"
        }
    ]
}
```
