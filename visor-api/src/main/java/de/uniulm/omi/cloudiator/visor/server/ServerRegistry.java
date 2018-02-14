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

package de.uniulm.omi.cloudiator.visor.server;

import java.io.IOException;
import javax.annotation.Nullable;

/**
 * Created by daniel on 11.11.15.
 */
public interface ServerRegistry {

  /**
   * Returns the server for this component instance id.
   * <p>
   * If such a server does not exist, it will be automatically created by the
   * registry.
   *
   * @param componentInstanceId the component instance to measure
   * @param port if the server does not exist, use this port to create it
   * @return a new server or an existing one.
   * @throws IOException if the creating of the new server fails.
   */
  Server getServer(String componentInstanceId, @Nullable Integer port) throws IOException;

  /**
   * Unregisters a server at this registry.
   *
   * @param server the server to unregister.
   */
  void unregister(Server server);

}
