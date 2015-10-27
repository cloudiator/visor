# /servers

## GET /servers
### Description
This action returns the currently active servers within the system.
### Example
```
[  
   {  
      monitorContext: {
         "local.ip": "134.60.30.150"
      },
      "port": 12345,
      "links":[  
         {  
            "href":"/servers/a5181ff0-dda1-45dd-8483-dfd604d0d38d",
            "rel":"self"
         }
      ]
   },
   {  
      monitorContext: {
          "local.ip": "134.60.30.150"
      },
      "port": 12346,
      "links":[  
         {  
            "href":"/servers/c7a98598-e64a-4f1f-8f28-054eb743ccf3",
            "rel":"self"
         }
      ]
   }
]
```
## GET /servers/{uuid}
### Description
This action returns the server registered under the given {uuid}.

### Parameters
Parameter | Type    | Description
----------|-------- |-------------
uuid   | string | The identifier of the server. Mandatory.
### Example
```
GET /servers/c7a98598-e64a-4f1f-8f28-054eb743ccf3
```
```
{  
      monitorContext: {
          "local.ip": "134.60.30.150"
      },
      "port": 12346,
      "links":[  
         {  
            "href":"/servers/c7a98598-e64a-4f1f-8f28-054eb743ccf3",
            "rel":"self"
         }
      ]
   }
```
## POST /servers
### Description
This action creates a new server. After successful creating the new server will be returned. See GET.
### Parameters
Parameter | Type    | Description
----------|-------- |-------------
monitorContext   | map| A map of key-value objects. Each key-value object represents a specific monitor context. Optional.
port | The port the server listens to.
### Example
```
POST /servers
```
```
{  
      monitorContext: {
          "local.ip": "134.60.30.150"
      },
      "port": 12346,
      "links":[  
         {  
            "href":"/servers/c7a98598-e64a-4f1f-8f28-054eb743ccf3",
            "rel":"self"
         }
      ]
   }
```

## PUT /monitors/{uuid}
### Description
This action creates a new server. After successful creation, the new server will be returned.

### Parameters
Parameter | Type    | Description
----------|-------- |-------------
monitorContext   | map| A map of key-value objects. Each key-value object represents a specific monitor context. Optional.
port | The port the server listens to.
uuid | String | Required. The identifier under which the server will be stored.
### Example
```
PUT /servers/c7a98598-e64a-4f1f-8f28-054eb743ccf3
```
```
{  
      monitorContext: {
          "local.ip": "134.60.30.150"
      },
      "port": 12346,
      "links":[  
         {  
            "href":"/servers/c7a98598-e64a-4f1f-8f28-054eb743ccf3",
            "rel":"self"
         }
      ]
   }
```


## DELETE /servers/{uuid}
### Description
Stops the server with the given {uuid}
### Example
```
DELETE /servers/c7a98598-e64a-4f1f-8f28-054eb743ccf3
```
