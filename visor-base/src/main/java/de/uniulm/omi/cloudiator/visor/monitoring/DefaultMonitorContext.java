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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.uniulm.omi.cloudiator.visor.config.ContextConstants.CLOUD_ID;
import static de.uniulm.omi.cloudiator.visor.config.ContextConstants.JAVA_VERSION;
import static de.uniulm.omi.cloudiator.visor.config.ContextConstants.LOCAL_IP;
import static de.uniulm.omi.cloudiator.visor.config.ContextConstants.OS_ARCH;
import static de.uniulm.omi.cloudiator.visor.config.ContextConstants.OS_NAME;
import static de.uniulm.omi.cloudiator.visor.config.ContextConstants.OS_VERS;
import static de.uniulm.omi.cloudiator.visor.config.ContextConstants.VM_ID;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Created by daniel on 23.10.15.
 */
class DefaultMonitorContext extends BaseMonitorContext {


  private final Map<String, String> defaultContext;

  DefaultMonitorContext(Map<String, String> context, String localIp, String vmId,
      @Nullable String cloudId) {
    super(context);

    checkNotNull(localIp, "localIp is null");
    checkArgument(!localIp.isEmpty(), "localIp is empty");

    checkNotNull(vmId, "vmId is null");
    checkArgument(!vmId.isEmpty(), "vmId is empty");

    this.defaultContext = new HashMap<>();
    defaultContext.put(LOCAL_IP, localIp);
    defaultContext.put(OS_NAME, System.getProperty(OS_NAME).replaceAll("\\s", ""));
    defaultContext.put(OS_ARCH, System.getProperty(OS_ARCH));
    defaultContext.put(OS_VERS, System.getProperty(OS_VERS));
    defaultContext.put(JAVA_VERSION, System.getProperty(JAVA_VERSION));
    if (cloudId != null) {
      defaultContext.put(CLOUD_ID, cloudId);
    }
    defaultContext.put(VM_ID, vmId);
  }

  @Override
  public Map<String, String> getContext() {

    //temporary map two filter duplicates as they are not allowed by the immutable map builder
    Map<String, String> temp = new HashMap<>(defaultContext.size() + super.getContext().size());
    temp.putAll(super.getContext());
    temp.putAll(defaultContext);

    return ImmutableMap.copyOf(temp);
  }


}
