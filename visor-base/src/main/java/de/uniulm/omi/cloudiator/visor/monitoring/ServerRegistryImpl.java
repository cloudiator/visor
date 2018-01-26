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

package de.uniulm.omi.cloudiator.visor.monitoring;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniulm.omi.cloudiator.visor.server.Server;
import de.uniulm.omi.cloudiator.visor.server.ServerFactory;
import de.uniulm.omi.cloudiator.visor.server.ServerRegistry;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by daniel on 11.11.15.
 */
@Singleton
public class ServerRegistryImpl implements ServerRegistry {

  static int LOWER_PORT_BOUNDARY = 49152;
  static int UPPER_PORT_BOUNDARY = 65535;


  private final ServerFactory serverFactory;
  private final BiMap<String, Server> servers;

  @Inject
  public ServerRegistryImpl(ServerFactory serverFactory) {
    this.serverFactory = serverFactory;
    servers = Maps.synchronizedBiMap(HashBiMap.create(new HashMap<>()));
  }

  @Override
  public Server getServer(String componentId) throws IOException {
    if (!servers.containsKey(componentId)) {
      Server server = serverFactory.createServer(LOWER_PORT_BOUNDARY, UPPER_PORT_BOUNDARY);
      servers.put(componentId, server);
    }
    return servers.get(componentId);
  }

  @Override
  public void unregister(Server server) {
    this.servers.inverse().remove(server);
  }
}
