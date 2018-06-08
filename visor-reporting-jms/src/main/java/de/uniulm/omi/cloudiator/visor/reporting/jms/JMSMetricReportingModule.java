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

package de.uniulm.omi.cloudiator.visor.reporting.jms;

import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.MetricReportingModule;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;

/**
 * Created by daniel on 01.12.16.
 */
public class JMSMetricReportingModule extends MetricReportingModule {

  @Override
  protected void configure() {
    super.configure();
    bind(JMSEncoding.class).to(MelodicJsonEncoding.class);
    bind(TopicSelector.class).to(MetricNameTopicSelector.class);
  }

  @Override
  protected Class<? extends ReportingInterface<Metric>> getReportingInterface() {
    return JMSReporter.class;
  }
}
