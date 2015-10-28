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

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 23.10.15.
 */
class DefaultMonitorContext extends BaseMonitorContext {

    private final Map<String, String> defaultContext;

    public static final String LOCAL_IP = "local.ip";
    public static final String OS_NAME = "os.name";
    public static final String OS_ARCH = "os.arch";
    public static final String OS_VERS = "os.version";
    public static final String JAVA_VERSION = "java.version";

    DefaultMonitorContext(Map<String, String> context, String localIp) {
        super(context);

        checkNotNull(localIp);
        checkArgument(!localIp.isEmpty());

        this.defaultContext = ImmutableMap
            .of(LOCAL_IP, localIp, OS_NAME, System.getProperty(OS_NAME), OS_ARCH,
                System.getProperty(OS_ARCH), OS_VERS, System.getProperty(OS_VERS), JAVA_VERSION,
                System.getProperty(JAVA_VERSION));
    }

    @Override public Map<String, String> getContext() {

        //temporary map two filter duplicates as they are not allowed by the immutable map builder
        Map<String, String> temp = new HashMap<>(defaultContext.size() + super.getContext().size());
        temp.putAll(super.getContext());
        temp.putAll(defaultContext);

        return ImmutableMap.copyOf(temp);
    }


}
