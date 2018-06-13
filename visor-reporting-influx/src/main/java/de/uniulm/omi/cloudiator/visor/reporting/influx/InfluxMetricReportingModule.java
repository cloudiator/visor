/*
 * Copyright (c) 2014-2016 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.reporting.influx;

import static com.google.common.base.Preconditions.checkArgument;

import de.uniulm.omi.cloudiator.visor.monitoring.DataSink.DataSinkConfiguration;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.ReportingInterfaceFactory;
import de.uniulm.omi.cloudiator.visor.reporting.MetricReportingModule;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

/**
 * Created by daniel on 01.12.16.
 */
public class InfluxMetricReportingModule extends MetricReportingModule {

  private static final InfluxReportingInterfaceFactory INSTANCE = new InfluxReportingInterfaceFactory();

  private static class InfluxReportingInterfaceFactory implements
      ReportingInterfaceFactory<Metric> {

    private static final String INFLUX_URL = "influx.url";
    private static final String INFLUX_USERNAME = "influx.username";
    private static final String INFLUX_PASSWORD = "influx.password";
    private static final String INFLUX_DATABASE = "influx.database";
    private static final String DEFAULT_INFLUX_DATABASE = "visor";

    private static final String VALIDATION_ERROR = "Expected configuration %s to contain %s.";

    @Override
    public ReportingInterface<Metric> of(DataSinkConfiguration dataSinkConfiguration) {

      validate(dataSinkConfiguration);
      final String influxUrl = dataSinkConfiguration.values().get(INFLUX_URL);
      final String influxUserName = dataSinkConfiguration.values().get(INFLUX_USERNAME);
      final String influxPassword = dataSinkConfiguration.values().get(INFLUX_PASSWORD);
      final String influxDatabaseName = dataSinkConfiguration.values()
          .getOrDefault(INFLUX_DATABASE, DEFAULT_INFLUX_DATABASE);

      InfluxDB connect = InfluxDBFactory.connect(influxUrl, influxUserName, influxPassword);
      if (connect.describeDatabases().stream().noneMatch(influxDatabaseName::equals)) {
        connect.createDatabase(influxDatabaseName);
      }

      return new InfluxReporter(influxDatabaseName, connect);
    }

    private void validate(DataSinkConfiguration dataSinkConfiguration) {
      checkArgument(dataSinkConfiguration.values().containsKey(INFLUX_URL),
          String.format(VALIDATION_ERROR, dataSinkConfiguration, INFLUX_URL));
      checkArgument(dataSinkConfiguration.values().containsKey(INFLUX_USERNAME),
          String.format(VALIDATION_ERROR, dataSinkConfiguration, INFLUX_USERNAME));
      checkArgument(dataSinkConfiguration.values().containsKey(INFLUX_PASSWORD),
          String.format(VALIDATION_ERROR, dataSinkConfiguration, INFLUX_PASSWORD));
    }

  }

  @Override
  protected void configure() {
    super.configure();
  }

  @Override
  protected ReportingInterfaceFactory<Metric> reportingInterfaceFactory() {
    return INSTANCE;
  }

  @Override
  protected String identifier() {
    return "influx";
  }
}
