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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Map;

/**
 * Created by daniel on 23.10.15.
 */
public class DefaultMonitorContextFactory implements MonitorContextFactory {

  @Inject(optional = true)
  @Named("cloudId")
  private String cloudId = null;

  private final String localIp;
  private final String vmId;

  @Inject
  DefaultMonitorContextFactory(@Named("localIp") String localIp, @Named("vmId") String vmId) {
    this.localIp = localIp;
    this.vmId = vmId;
  }

  @Override
  public MonitorContext create(Map<String, String> context) {
    return new DefaultMonitorContext(context, localIp, vmId, cloudId);
  }


}
