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

package de.uniulm.omi.cloudiator.visor;/*
 *
 *  * Copyright (c) 2014 University of Ulm
 *  *
 *  * See the NOTICE file distributed with this work for additional information
 *  * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import de.uniulm.omi.cloudiator.visor.execution.impl.ShutdownHook;
import de.uniulm.omi.cloudiator.visor.monitoring.api.InvalidMonitorContextException;
import de.uniulm.omi.cloudiator.visor.monitoring.api.MonitoringService;
import de.uniulm.omi.cloudiator.visor.monitoring.api.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.api.SensorNotFoundException;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.Interval;
import de.uniulm.omi.cloudiator.visor.rest.RestServer;
import de.uniulm.omi.cloudiator.visor.server.impl.SocketServer;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 17.12.14.
 */
public class MonitoringAgentService {

  private final Set<Module> modules;

  public MonitoringAgentService(Set<Module> modules) {
    this.modules = modules;
  }

  public void start() {
    final Injector injector = Guice.createInjector(this.modules);
    try {
      injector.getInstance(MonitoringService.class)
          .startMonitoring(UUID.randomUUID().toString(), "cpu_usage",
              "de.uniulm.omi.cloudiator.visor.monitoring.sensors.CpuUsageSensor",
              new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
      injector.getInstance(MonitoringService.class)
          .startMonitoring(UUID.randomUUID().toString(), "memory_usage",
              "de.uniulm.omi.cloudiator.visor.monitoring.sensors.MemoryUsageSensor",
              new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());

      //MYSQL sensors
      //injector.getInstance(MonitoringService.class).startMonitoring("mysql_nb_failed_connections", "de.uniulm.omi.cloudiator.visor.monitoring.sensors.mysqlsensors.NbFailedConnectionsMySQLSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
      //injector.getInstance(MonitoringService.class).startMonitoring("mysql_nb_queries", "de.uniulm.omi.cloudiator.visor.monitoring.sensors.mysqlsensors.NbQueriesMySQLSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
      //injector.getInstance(MonitoringService.class).startMonitoring("mysql_prc_allowed_connections", "de.uniulm.omi.cloudiator.visor.monitoring.sensors.mysqlsensors.PercentAllowedConnectionsMySQLSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
      //injector.getInstance(MonitoringService.class).startMonitoring("mysql_prc_full_table_scan", "de.uniulm.omi.cloudiator.visor.monitoring.sensors.mysqlsensors.PercentageOfTableScanMySQLSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());

      //LogFile sensors
      //injector.getInstance(MonitoringService.class).startMonitoring("haproxy_log", "de.uniulm.omi.cloudiator.visor.monitoring.sensors.logsensors.HaproxyLogSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
      //injector.getInstance(MonitoringService.class).startMonitoring("ofbiz_log",   "de.uniulm.omi.cloudiator.visor.monitoring.sensors.logsensors.OFBizLogSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
      //injector.getInstance(MonitoringService.class).startMonitoring("mysql_log",   "de.uniulm.omi.cloudiator.visor.monitoring.sensors.logsensors.MySQLLogSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());

      //JMXBean sensors
      //injector.getInstance(MonitoringService.class).startMonitoring("heapmemory_usage", "de.uniulm.omi.cloudiator.visor.monitoring.sensors.jmxsensors.HeapMemoryUsageJMXSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
      //injector.getInstance(MonitoringService.class).startMonitoring("peakthread_count", "de.uniulm.omi.cloudiator.visor.monitoring.sensors.jmxsensors.PeakThreadCountJMXSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());
      //injector.getInstance(MonitoringService.class).startMonitoring("uptime", "de.uniulm.omi.cloudiator.visor.monitoring.sensors.jmxsensors.UpTimeJMXSensor", new Interval(1, TimeUnit.SECONDS), Collections.<String, String>emptyMap());

    } catch (SensorNotFoundException | InvalidMonitorContextException | SensorInitializationException e) {
      throw new RuntimeException(e);
    }
    injector.getInstance(SocketServer.class);
    injector.getInstance(RestServer.class);
    Runtime.getRuntime().addShutdownHook(injector.getInstance(ShutdownHook.class));
  }
}
