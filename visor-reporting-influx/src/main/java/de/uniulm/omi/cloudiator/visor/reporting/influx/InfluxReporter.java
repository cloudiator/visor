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

import static com.google.common.base.Preconditions.checkNotNull;

import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import java.util.Collection;
import org.influxdb.InfluxDB;

/**
 * Created by daniel on 01.12.16.
 */
public class InfluxReporter implements ReportingInterface<Metric> {

  private final String database;
  private final MetricToPoint converter = MetricToPoint.getInstance();
  private final InfluxDB influxDB;

  public InfluxReporter(String database,
      InfluxDB influxDB) {

    checkNotNull(database, "database is null");
    this.database = database;
    this.influxDB = influxDB;
  }

  @Override
  public void report(Metric item) throws ReportingException {
    influxDB.write(database, "autogen", converter.apply(item));
  }

  @Override
  public void report(Collection<Metric> items) throws ReportingException {
    for (Metric metric : items) {
      report(metric);
    }
  }
}
