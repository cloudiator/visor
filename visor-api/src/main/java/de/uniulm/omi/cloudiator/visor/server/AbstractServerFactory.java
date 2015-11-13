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
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Created by daniel on 23.10.15.
 */
public abstract class AbstractServerFactory implements ServerFactory {

    @Override public synchronized final Server createServer(int lower, int upper)
        throws IOException {
        for (int i = lower; i <= upper; i++) {
            try (ServerSocket serverSocket = new ServerSocket(i);
                DatagramSocket datagramSocket = new DatagramSocket(i)) {
                serverSocket.setReuseAddress(true);
                datagramSocket.setReuseAddress(true);
                serverSocket.close();
                datagramSocket.close();
                return createServer(i);
            } catch (IOException ignored) {
            }
        }
        throw new IOException(
            String.format("Could not find an empty port in the range %s-%s", lower, upper));
    }
}
