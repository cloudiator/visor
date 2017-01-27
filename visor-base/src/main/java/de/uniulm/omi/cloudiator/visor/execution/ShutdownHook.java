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

package de.uniulm.omi.cloudiator.visor.execution;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daniel on 15.12.14.
 */
public class ShutdownHook extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private final ExecutionService executionService;
    private final int reportingInterval;

    @Inject public ShutdownHook(ExecutionService executionService,
        @Named("reportingInterval") int reportingInterval) {
        this.executionService = executionService;
        this.reportingInterval = reportingInterval;
    }

    @Override public void run() {
        logger.debug("Running shutdown hook. Using reporting Interval*2 (" + reportingInterval * 2
            + ") as timeout for the execution.");
        this.executionService.shutdown(reportingInterval * 2);
    }
}
