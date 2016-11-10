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

import de.uniulm.omi.cloudiator.visor.exceptions.MonitorException;
import de.uniulm.omi.cloudiator.visor.monitoring.Monitor;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitoringService;
import de.uniulm.omi.cloudiator.visor.rest.converters.MonitorConverters;
import de.uniulm.omi.cloudiator.visor.rest.entities.MonitorDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 06.02.15.
 */
@Path("/monitors") public class MonitorController {

    private final MonitoringService monitoringService;
    private static final Logger LOGGER = LogManager.getLogger(MonitorController.class);

    public MonitorController(final MonitoringService monitoringService) {

        checkNotNull(monitoringService);
        this.monitoringService = monitoringService;
    }

    @GET @Produces(MediaType.APPLICATION_JSON) public Collection<MonitorDto> getMonitors() {
        return monitoringService.getMonitors().stream().map(monitor -> getMonitor(monitor.uuid()))
            .collect(Collectors.toList());
    }

    @GET @Produces(MediaType.APPLICATION_JSON) @Path("/{uuid}")
    public MonitorDto getMonitor(@PathParam("uuid") String uuid) {

        Optional<Monitor> monitor = monitoringService.getMonitor(uuid);

        if (!monitor.isPresent()) {
            throw new NotFoundException();
        }

        return MonitorConverters.getConverter(monitor.get().getClass()).apply(monitor.get());

    }


    @PUT @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uuid}")
    public MonitorDto putMonitor(@PathParam("uuid") String uuid, MonitorDto monitor) {

        if (this.monitoringService.isMonitoring(uuid)) {
            this.monitoringService.stopMonitor(uuid);
        }

        try {
            monitor.start(uuid, monitoringService);
        } catch (MonitorException e) {
            LOGGER.error("Could not create monitor.", e);
            throw new BadRequestException(e);
        }

        return getMonitor(uuid);
    }

    @POST @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public MonitorDto postMonitor(MonitorDto monitor) {

        //generate a random uuid for the monitor
        final UUID uuid = UUID.randomUUID();

        return this.putMonitor(uuid.toString(), monitor);

    }

    @DELETE @Produces(MediaType.APPLICATION_JSON) @Path("/{uuid}")
    public void deleteMonitor(@PathParam("uuid") String uuid) {
        if (!monitoringService.isMonitoring(uuid)) {
            throw new BadRequestException();
        }
        this.monitoringService.stopMonitor(uuid);
    }

    @DELETE @Produces(MediaType.APPLICATION_JSON) public void deleteAllMonitors() {
        for (Monitor monitor : this.monitoringService.getMonitors()) {
            this.monitoringService.stopMonitor(monitor.uuid());
        }
    }

}
