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

/**
 * Created by daniel on 23.10.15.
 */
public interface ServerFactory {

  /**
   * Starts a new server on the given port.
   *
   * @param port the port used by the server.
   * @return the created server
   * @throws IOException if an I/O error occurs while opening the socket.
   * @see java.net.ServerSocket
   */
  Server createServer(int port) throws IOException;

  /**
   * Starts a new server using a free port from the given port range.
   *
   * @param lower lower boundary of the port range (included)
   * @param upper upper boundary of the port range (included)
   * @return the created server
   * @throws IOException if no empty port could be found on the given port range.
   * @see java.net.ServerSocket
   */
  Server createServer(int lower, int upper)
      throws IOException;

}
