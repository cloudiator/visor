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

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitoringService;
import de.uniulm.omi.cloudiator.visor.rest.converters.ServerConverter;
import de.uniulm.omi.cloudiator.visor.rest.entities.Rel;
import de.uniulm.omi.cloudiator.visor.rest.entities.ServerDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 23.10.15.
 */
@Path("/servers") public class ServerController {

    private final MonitoringService monitoringService;

    @Inject public ServerController(MonitoringService monitoringService) {
        checkNotNull(monitoringService);
        this.monitoringService = monitoringService;
    }

    @GET @Produces(MediaType.APPLICATION_JSON)
    public Collection<ResponseWrapper<ServerDto>> getServers() {

        return monitoringService.getServers().stream().map(server -> getServer(server.uuid()))
            .collect(Collectors.toList());

    }

    @GET @Produces(MediaType.APPLICATION_JSON) @Path("/{uuid}")
    public ResponseWrapper<ServerDto> getServer(@PathParam("uuid") String uuid) {


        if (this.monitoringService.getServer(uuid) == null) {
            throw new NotFoundException();
        }

        return ResponseBuilder.newBuilder(ServerDto.class)
            .entity(new ServerConverter().apply(this.monitoringService.getServer(uuid)))
            .addLink(String.format("/servers/%s", uuid), Rel.SELF).build();
    }


    @PUT @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uuid}")
    public ResponseWrapper<ServerDto> putServer(@PathParam("uuid") String uuid, ServerDto server) {

        if (this.monitoringService.getServer(uuid) != null) {
            this.monitoringService.stopServer(uuid);
        }

        try {
            this.monitoringService.startServer(uuid, server.getMonitorContext(), server.getPort());
        } catch (IOException e) {
            throw new BadRequestException(e);
        }


        return getServer(uuid);
    }

    @POST @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public ResponseWrapper<ServerDto> postServer(ServerDto server) {

        //generate a random uuid for the monitor
        final UUID uuid = UUID.randomUUID();

        return this.putServer(uuid.toString(), server);

    }

    @DELETE @Produces(MediaType.APPLICATION_JSON) @Path("/{uuid}")
    public void deleteServer(@PathParam("uuid") String uuid) {
        this.monitoringService.stopServer(uuid);
    }

    @DELETE @Produces(MediaType.APPLICATION_JSON) public void deleteAllMonitors() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

}
