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

package de.uniulm.omi.cloudiator.visor.config;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import de.uniulm.omi.cloudiator.visor.execution.DefaultScheduledExecutionService;
import de.uniulm.omi.cloudiator.visor.execution.ExecutionService;
import de.uniulm.omi.cloudiator.visor.execution.ScheduledExecutionService;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorFactory;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorFactoryImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitoringService;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitoringServiceImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorFactory;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorFactoryImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.ServerRegistryImpl;
import de.uniulm.omi.cloudiator.visor.reporting.ExternalReporting;
import de.uniulm.omi.cloudiator.visor.reporting.MultiDataSinkReportingInterface;
import de.uniulm.omi.cloudiator.visor.reporting.Queue;
import de.uniulm.omi.cloudiator.visor.reporting.QueueWorkerFactory;
import de.uniulm.omi.cloudiator.visor.reporting.QueueWorkerFactoryInterface;
import de.uniulm.omi.cloudiator.visor.reporting.QueuedReporting;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import de.uniulm.omi.cloudiator.visor.server.ServerRegistry;

/**
 * Created by daniel on 08.04.15.
 */
public class BaseModule extends AbstractModule {

  private final ConfigurationAccess configurationAccess;
  private final CommandLinePropertiesAccessor commandLinePropertiesAccessor;

  public BaseModule(ConfigurationAccess configurationAccess,
      CommandLinePropertiesAccessor commandLinePropertiesAccessor) {
    checkNotNull(configurationAccess);
    checkNotNull(commandLinePropertiesAccessor);
    this.commandLinePropertiesAccessor = commandLinePropertiesAccessor;
    this.configurationAccess = configurationAccess;
  }

  @Override
  protected void configure() {
    install(new ConfigurationModule(configurationAccess, commandLinePropertiesAccessor));
    install(new IpModule());
    bind(ExecutionService.class).to(DefaultScheduledExecutionService.class);
    bind(new TypeLiteral<ReportingInterface<Metric>>() {
    }).annotatedWith(QueuedReporting.class).to(new TypeLiteral<Queue<Metric>>() {
    });
    bind(new TypeLiteral<ReportingInterface<Metric>>() {
    }).annotatedWith(ExternalReporting.class).to(MultiDataSinkReportingInterface.class);
    bind(new TypeLiteral<QueueWorkerFactoryInterface<Metric>>() {
    }).to(new TypeLiteral<QueueWorkerFactory<Metric>>() {
    });
    bind(MonitoringService.class).to(MonitoringServiceImpl.class);
    bind(ScheduledExecutionService.class).to(DefaultScheduledExecutionService.class);
    bind(MonitorFactory.class).to(MonitorFactoryImpl.class);
    bind(SensorFactory.class).to(SensorFactoryImpl.class);
    bind(ServerRegistry.class).to(ServerRegistryImpl.class);
  }


}
