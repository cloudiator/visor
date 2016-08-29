/*
 * Copyright (c) 2014-2016 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.reporting.chukwa;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.visor.exceptions.ConfigurationException;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 22.02.16.
 */
public class ChukwaReporter implements ReportingInterface<Metric> {

    private static final Logger LOGGER = LogManager.getLogger(ChukwaReporter.class);

    private final URI chukwaUri;
    private final String vmID;

    @Inject
    public ChukwaReporter(@Named("chukwaUrl") String chuwkaUrl, @Named("chukwaVmId") String vmId) {
        checkNotNull(vmId);
        checkArgument(!vmId.isEmpty());
        this.vmID = vmId;
        try {
            this.chukwaUri = new URI(chuwkaUrl);
        } catch (URISyntaxException e) {
            throw new ConfigurationException(e);
        }
    }

    @Override public void report(Metric item) throws ReportingException {

        try (ChukwaClient chukwaClient = new ChukwaClient(chukwaUri)) {

        	ChukwaRequest chukwaRequest = MetricToChukwa.parse(vmID, item);
        	
            // final String encodedMetric = new MetricToChukwa(vmID).apply(item);

            final HttpResponse response = chukwaClient.post(chukwaRequest);

            LOGGER.debug(String
                .format("Sending request %s for metric %s to chukwa, got response %s.",
                    chukwaRequest, item, response.getStatusLine().toString()));

        } catch (IOException e) {
            throw new ReportingException(e);
        }
    }

    @Override public void report(Collection<Metric> items) throws ReportingException {
        for (Metric metric : items) {
            this.report(metric);
        }
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this).add("chukwaUri", chukwaUri).add("vmID", vmID)
            .toString();
    }
}
