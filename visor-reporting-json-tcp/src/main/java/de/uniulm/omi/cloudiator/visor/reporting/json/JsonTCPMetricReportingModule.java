/*
 * Copyright (c) 2014-2017 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.reporting.json;

import de.uniulm.omi.cloudiator.visor.monitoring.DataSink.DataSinkConfiguration;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.ReportingInterfaceFactory;
import de.uniulm.omi.cloudiator.visor.reporting.MetricReportingModule;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;

/**
 * Created by daniel on 10.12.14.
 */
public class JsonTCPMetricReportingModule extends MetricReportingModule {

  private static final JsonTCPReportingFactory INSTANCE = new JsonTCPReportingFactory();

  private static class JsonTCPReportingFactory implements ReportingInterfaceFactory<Metric> {


    private static final String TCP_SERVER = "json-tcp.server";
    private static final String TCP_PORT = "json-tcp.port";

    @Override
    public ReportingInterface<Metric> of(DataSinkConfiguration dataSinkConfiguration) {
      return null;
    }

    private void validate(DataSinkConfiguration dataSinkConfiguration) {
      
    }
  }

  @Override
  protected ReportingInterfaceFactory<Metric> reportingInterfaceFactory() {
    return INSTANCE;
  }

  @Override
  protected String identifier() {
    return "json-tcp";
  }
}
