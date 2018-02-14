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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

/**
 * Created by daniel on 01.12.16.
 */
public class InfluxDbProvider implements Provider<InfluxDB> {

  private final String influxUrl;
  private final String influxUserName;
  private final String influxPassword;
  private final String influxDatabaseName;

  @Inject
  InfluxDbProvider(@Named("influxUrl") String influxUrl,
      @Named("influxUserName") String influxUserName,
      @Named("influxPassword") String influxPassword,
      @Named("influxDatabaseName") String influxDatabaseName) {

    checkNotNull(influxUrl, "influxUrl is null");
    checkNotNull(influxUserName, "influxUserName is null");
    checkNotNull(influxPassword, "influxPassword is null");
    checkNotNull(influxDatabaseName, "influxDatabaseName is null");

    this.influxUrl = influxUrl;
    this.influxUserName = influxUserName;
    this.influxPassword = influxPassword;
    this.influxDatabaseName = influxDatabaseName;
  }

  @Override
  public InfluxDB get() {

    InfluxDB connect = InfluxDBFactory.connect(influxUrl, influxUserName, influxPassword);
    if (connect.describeDatabases().stream().noneMatch(influxDatabaseName::equals)) {
      connect.createDatabase(influxDatabaseName);
    }

    return connect;
  }
}
