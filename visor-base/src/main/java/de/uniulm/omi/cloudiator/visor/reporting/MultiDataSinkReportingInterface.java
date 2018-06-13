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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniulm.omi.cloudiator.visor.monitoring.DataSink;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.monitoring.ReportingInterfaceFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MultiDataSinkReportingInterface implements ReportingInterface<Metric> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(MultiDataSinkReportingInterface.class);

  private final Map<String, ReportingInterfaceFactory<Metric>> factories;
  private static Cache<DataSink, ReportingInterface<Metric>> cache = CacheBuilder.newBuilder()
      .expireAfterAccess(10, TimeUnit.MINUTES).removalListener(
          (RemovalListener<DataSink, ReportingInterface<Metric>>) notification -> {
            final ReportingInterface<Metric> reportingInterface = notification.getValue();
            if (reportingInterface instanceof QueuedReportingInterface) {
              ((QueuedReportingInterface<Metric>) reportingInterface).cancel();
            }
          }).build();
  private final QueueFactory<Metric> queueFactory;

  private ReportingInterface<Metric> getInterface(DataSink dataSink) {
    try {
      return cache.get(dataSink, new Callable<ReportingInterface<Metric>>() {
        @Override
        public ReportingInterface<Metric> call() throws Exception {
          final ReportingInterface<Metric> original = factories.get(dataSink.type())
              .of(dataSink.config());
          return queueFactory
              .queueReportingInterface(original);
        }
      });
    } catch (ExecutionException e) {
      throw new IllegalStateException(e);
    }
  }

  @Inject
  public MultiDataSinkReportingInterface(
      Map<String, ReportingInterfaceFactory<Metric>> factories,
      QueueFactory<Metric> queueFactory) {
    this.factories = factories;
    this.queueFactory = queueFactory;
  }

  @Override
  public void report(Metric item) throws ReportingException {

    for (DataSink dataSink : item.monitor().dataSinks()) {
      try {
        getInterface(dataSink).report(item);
      } catch (Exception ignored) {
        LOGGER.warn(String
            .format("Exception while reporting metric %s to data sink %s. Ignoring.", item,
                dataSink), ignored);
      }
    }
  }

  @Override
  public void report(Collection<Metric> items) throws ReportingException {

    final Map<DataSink, List<Metric>> metricPartition = partitionMetrics(items);

    for (Entry<DataSink, List<Metric>> entry : metricPartition.entrySet()) {
      getInterface(entry.getKey()).report(entry.getValue());
    }
  }

  private Map<DataSink, List<Metric>> partitionMetrics(Collection<Metric> metrics) {

    Map<DataSink, List<Metric>> map = new HashMap<>();

    for (Metric metric : metrics) {
      for (DataSink dataSink : metric.monitor().dataSinks()) {
        if (!map.containsKey(dataSink)) {
          map.put(dataSink, new LinkedList<>());
        }
        final List<Metric> metricList = map.get(dataSink);
        metricList.add(metric);
      }
    }
    return map;
  }


}
