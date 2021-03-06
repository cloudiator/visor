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

package de.uniulm.omi.cloudiator.visor.reporting;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.ReportingInterfaceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daniel on 10.12.14.
 */
public abstract class MetricReportingModule extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(MetricReportingModule.class);

  @Override
  protected void configure() {

    MapBinder<String, ReportingInterfaceFactory<Metric>> mapBinder = MapBinder
        .newMapBinder(binder(), new TypeLiteral<String>() {
        }, new TypeLiteral<ReportingInterfaceFactory<Metric>>() {
        });

    LOGGER.info(String
        .format("Adding reporting module binding: identifier %s -> reportingInterface %s",
            identifier(), reportingInterfaceFactory()));
    mapBinder.addBinding(identifier()).toInstance(reportingInterfaceFactory());
  }

  protected abstract ReportingInterfaceFactory<Metric> reportingInterfaceFactory();

  protected abstract String identifier();


}
