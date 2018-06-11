# DataSinks

## Kairos Database

Reports metrics to a kairos database.

Configuration Option | Description
---- | ------------
kairos.port | The port of the kairos database.
kairos.host | The hostname of the kairos database.

## Influx Database

Reports metrics to a Influx database.

Configuration Option | Description
---- | ------------
influx.url | The connection URL of the influx database.
influx.username | The username to use for connecting to the database.
influx.password | The password of above configured user.
influx.database | (optional) The database to use. Will be auto-generated if it does not exist. Defaults to visor.

## Command Line

Prints the metrics to the stdout.

## JMS

todo

## JSON TCP

todo
