/*
 * Copyright (c) 2014-2018 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.reporting;

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.ReportingInterfaceFactory;
import java.util.Collection;
import java.util.Map;

/**
 * @// TODO: 11.06.18 implement caching of reporters based on data sink configurations
 * @// TODO: 11.06.18 implement collection report by grouping by configurations
 */
public class MultiDataSinkReportingInterface implements ReportingInterface<Metric> {

  private final Map<String, ReportingInterfaceFactory<Metric>> factories;

  @Inject
  public MultiDataSinkReportingInterface(
      Map<String, ReportingInterfaceFactory<Metric>> factories) {
    this.factories = factories;
  }

  @Override
  public void report(Metric item) throws ReportingException {
    factories.get(item.monitor().dataSink().type()).of(item.monitor().dataSink().config())
        .report(item);
  }

  @Override
  public void report(Collection<Metric> items) throws ReportingException {
    for (Metric item : items) {
      report(item);
    }
  }
}
