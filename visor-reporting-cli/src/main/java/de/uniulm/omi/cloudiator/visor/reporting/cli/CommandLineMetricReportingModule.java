/*
 * Copyright (c) 2014-2015 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.reporting.cli;


import de.uniulm.omi.cloudiator.visor.monitoring.DataSink.DataSinkConfiguration;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.ReportingInterfaceFactory;
import de.uniulm.omi.cloudiator.visor.reporting.MetricReportingModule;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;

/**
 * Created by daniel on 15.12.14.
 */
public class CommandLineMetricReportingModule extends MetricReportingModule {

  private static final String IDENTIFIER = "CLI";
  private static final CommandLineReporterFactory FACTORY = new CommandLineReporterFactory();

  private static class CommandLineReporterFactory implements ReportingInterfaceFactory<Metric> {

    private final static CommandLineReporter INSTANCE = new CommandLineReporter();

    @Override
    public ReportingInterface<Metric> of(DataSinkConfiguration dataSinkConfiguration) {
      return INSTANCE;
    }
  }

  @Override
  protected ReportingInterfaceFactory<Metric> reportingInterfaceFactory() {
    return FACTORY;
  }

  @Override
  protected String identifier() {
    return IDENTIFIER;
  }
}
