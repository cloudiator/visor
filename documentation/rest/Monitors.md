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
### SensorMonitor
#### Parameters
#### Example


### PushMonitor
#### Parameters
#### Example
