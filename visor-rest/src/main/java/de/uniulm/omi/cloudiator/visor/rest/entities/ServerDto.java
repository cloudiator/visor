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

package de.uniulm.omi.cloudiator.visor.rest.entities;

import java.util.Collections;
import java.util.Map;

/**
 * Created by daniel on 27.10.15.
 */
public class ServerDto {

    private Map<String, String> monitorContext;
    private Integer port;

    private ServerDto() {
    }

    public ServerDto(Map<String, String> monitorContext, int port) {
        this.monitorContext = monitorContext;
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public Map<String, String> getMonitorContext() {
        if (monitorContext == null) {
            return Collections.emptyMap();
        }
        return monitorContext;
    }

    public void setMonitorContext(Map<String, String> monitorContext) {
        this.monitorContext = monitorContext;
    }

}
