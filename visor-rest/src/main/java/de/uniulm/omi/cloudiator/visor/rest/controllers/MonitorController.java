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

package de.uniulm.omi.cloudiator.visor.rest.controllers;

import com.google.common.collect.Collections2;
import de.uniulm.omi.cloudiator.visor.monitoring.*;
import de.uniulm.omi.cloudiator.visor.monitoring.Monitor;
import de.uniulm.omi.cloudiator.visor.rest.converters.MonitorToMonitorJsonConverter;
import de.uniulm.omi.cloudiator.visor.rest.resources.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 06.02.15.
 */
@Path("/") public class MonitorController {

    private final MonitoringService monitoringService;

    public MonitorController(final MonitoringService monitoringService) {
        checkNotNull(monitoringService);
        this.monitoringService = monitoringService;
    }

    @GET @Produces(MediaType.APPLICATION_JSON) @Path("/monitors")
    public Collection<MonitorEntity> getMonitors() {
        return Collections2
            .transform(monitoringService.getMonitors(), new MonitorToMonitorJsonConverter());
    }

    @GET @Produces(MediaType.APPLICATION_JSON) @Path("/monitors/{uuid}")
    public MonitorEntity getMonitor(@PathParam("uuid") String uuid) {


        if (this.monitoringService.getMonitor(uuid) == null) {
            throw new NotFoundException();
        }

        return new MonitorWithLinks(
            new MonitorToMonitorJsonConverter().apply(monitoringService.getMonitor(uuid)),
            Links.selfLink("/monitors/" + uuid));
    }


    @PUT @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    @Path("/monitors/{uuid}")
    public MonitorEntity putMonitor(@PathParam("uuid") String uuid, BaseMonitor monitor) {

        DefaultMonitorContext.MonitorContextBuilder builder = DefaultMonitorContext.builder();
        for (Context context : monitor.getContexts()) {
            builder.addContext(context.getKey(), context.getValue());
        }

        //if we are already monitoring, we restart
        if (this.monitoringService.isMonitoring(uuid)) {
            this.monitoringService.stopMonitoring(uuid);
        }
        try {
            this.monitoringService
                .startMonitoring(uuid, monitor.getMetricName(), monitor.getSensorClassName(),
                		monitor.getSensorSourceUri(), new DefaultInterval(monitor.getInterval().getPeriod(),
                        monitor.getInterval().getTimeUnit()), builder.build().getContext());
        } catch (SensorNotFoundException | SensorInitializationException |
            InvalidMonitorContextException e)
        {
            throw new BadRequestException(e);
        }
        return new MonitorToMonitorJsonConverter().apply(this.monitoringService.getMonitor(uuid));
    }

    @POST @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    @Path("/monitors") public MonitorEntity postMonitor(BaseMonitor monitor) {

        //generate a random name for the monitor
        final UUID uuid = UUID.randomUUID();

        return this.putMonitor(uuid.toString(), monitor);

    }

    @DELETE @Produces(MediaType.APPLICATION_JSON) @Path("/monitors/{uuid}")
    public void deleteMonitor(@PathParam("uuid") String uuid) {
        this.monitoringService.stopMonitoring(uuid);
    }

    @DELETE @Produces(MediaType.APPLICATION_JSON) @Path("/monitors")
    public void deleteAllMonitors() {
        for (Monitor monitor : this.monitoringService.getMonitors()) {
            this.monitoringService.stopMonitoring(monitor.getUuid());
        }
    }

}
