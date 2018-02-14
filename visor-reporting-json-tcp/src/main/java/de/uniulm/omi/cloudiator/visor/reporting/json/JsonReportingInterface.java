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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Collection;

/**
 * Created by daniel on 28.06.17.
 */
public class JsonReportingInterface implements ReportingInterface<Metric> {

  private final Socket socket;

  private static class DecoratedMetric {

    @JsonUnwrapped
    public Metric metric;

    public String type = "visor";


    public DecoratedMetric(Metric metric) {
      this.metric = metric;
    }

  }

  @Inject
  public JsonReportingInterface(@Named("jsonTcpServer") String tcpServer,
      @Named("jsonTcpPort") int port) {

    checkNotNull(tcpServer, "tcpServer is null");
    checkArgument(!tcpServer.isEmpty());

    try {
      socket = new Socket(tcpServer, port);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void report(Metric item) throws ReportingException {
    ObjectMapper om = new ObjectMapper();
    try {
      String metricString = om.writeValueAsString(new DecoratedMetric(item));
      OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8);
      osw.write(metricString);
      osw.flush();
    } catch (JsonProcessingException e) {
      throw new ReportingException(String.format("Could not convert metric %s to json", item), e);
    } catch (IOException e) {
      throw new ReportingException(String.format("Could not write metric %s to socket", item), e);
    }
  }

  @Override
  public void report(Collection<Metric> items) throws ReportingException {
    for (Metric metric : items) {
      report(metric);
    }
  }
}
