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

package de.uniulm.omi.executionware.agent.rest.controllers;

import com.google.common.collect.Collections2;
import de.uniulm.omi.executionware.agent.monitoring.api.InvalidMonitorContextException;
import de.uniulm.omi.executionware.agent.monitoring.api.MonitoringService;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorInitializationException;
import de.uniulm.omi.executionware.agent.monitoring.api.SensorNotFoundException;
import de.uniulm.omi.executionware.agent.monitoring.impl.Interval;
import de.uniulm.omi.executionware.agent.monitoring.impl.MonitorContext;
import de.uniulm.omi.executionware.agent.rest.converters.MonitorToMonitorJsonConverter;
import de.uniulm.omi.executionware.agent.rest.resources.Context;
import de.uniulm.omi.executionware.agent.rest.resources.Monitor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 06.02.15.
 */
@Path("/")
public class MonitorController {

    private final MonitoringService monitoringService;

    public MonitorController(final MonitoringService monitoringService) {
        checkNotNull(monitoringService);
        this.monitoringService = monitoringService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/monitors")
    public Collection<Monitor> getMonitors() {
        return Collections2.transform(monitoringService.getMonitors(), new MonitorToMonitorJsonConverter());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/monitors/{metricName}")
    public Monitor getMonitor(@PathParam("metricName") String metricName) {
        return new MonitorToMonitorJsonConverter().apply(monitoringService.getMonitor(metricName));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/monitors")
    public Monitor createMonitor(Monitor monitor) throws InvalidMonitorContextException, SensorInitializationException, SensorNotFoundException {

        MonitorContext.MonitorContextBuilder builder = MonitorContext.builder();
        for (Context context : monitor.getContexts()) {
            builder.addContext(context.getKey(), context.getValue());
        }
        this.monitoringService.startMonitoring(monitor.getMetricName(), monitor.getSensorClassName(), new Interval(monitor.getInterval().getPeriod(), monitor.getInterval().getTimeUnit()), builder.build().getContext());
        return new MonitorToMonitorJsonConverter().apply(this.monitoringService.getMonitor(monitor.getMetricName()));
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/monitors/{metricName}")
    public void deleteMonitor(@PathParam("metricName") String metricName) {
        this.monitoringService.stopMonitoring(metricName);
    }

}
