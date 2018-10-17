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

package de.uniulm.omi.cloudiator.visor.reporting.kairos;


import static com.google.common.base.Preconditions.checkArgument;

import de.uniulm.omi.cloudiator.visor.monitoring.DataSink.DataSinkConfiguration;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.ReportingInterfaceFactory;
import de.uniulm.omi.cloudiator.visor.reporting.MetricReportingModule;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;

/**
 * Created by daniel on 10.12.14.
 */
public class KairosMetricReportingModule extends MetricReportingModule {

  private static final String IDENTIFIER = "KAIROS";

  private static class KairosFactory implements ReportingInterfaceFactory<Metric> {

    private static final KairosFactory INSTANCE = new KairosFactory();
    private static final String KAIROS_PORT = "kairos.port";
    private static final String KAIROS_HOST = "kairos.host";
    private static final String VALIDATION_ERROR = "Expected configuration %s to contain %s.";

    @Override
    public ReportingInterface<Metric> of(DataSinkConfiguration dataSinkConfiguration) {
      validate(dataSinkConfiguration);
      return new KairosDb(dataSinkConfiguration.values().get(KAIROS_HOST),
          Integer.valueOf(dataSinkConfiguration.values().get(KAIROS_PORT)));
    }

    private void validate(DataSinkConfiguration dataSinkConfiguration) {
      checkArgument(dataSinkConfiguration.values().containsKey(KAIROS_PORT),
          String.format(VALIDATION_ERROR, dataSinkConfiguration, KAIROS_PORT));
      checkArgument(dataSinkConfiguration.values().containsKey(KAIROS_HOST),
          String.format(VALIDATION_ERROR, dataSinkConfiguration, KAIROS_HOST));
      try {
        Integer.valueOf(dataSinkConfiguration.values().get(KAIROS_PORT));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(String
            .format("%s provided (%s) is not a number", KAIROS_PORT,
                dataSinkConfiguration.values().get(KAIROS_PORT)), e);
      }
    }
  }

  @Override
  protected ReportingInterfaceFactory<Metric> reportingInterfaceFactory() {
    return KairosFactory.INSTANCE;
  }

  @Override
  protected String identifier() {
    return IDENTIFIER;
  }
}
