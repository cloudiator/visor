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

package de.uniulm.omi.cloudiator.visor.sensors.haproxy;

import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.AbstractSensor;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;
import org.apache.commons.csv.CSVFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by daniel on 20.01.16.
 */
public class HaProxySensor extends AbstractSensor {

    private final static String HAPROXY_STATS_URL_CONFIG = "haproxy.url";
    private final static String HAPROXY_STATS_AUTH_USERNAME = "haproxy.auth.username";
    private final static String HAPROXY_STATS_AUTH_PASSWORD = "haproxy.auth.password";
    private final static CSVFormat csvFormat = CSVFormat.DEFAULT;
    private URL haProxyStatsUrl;
    private Optional<String> authentication;

    @Override protected void initialize(MonitorContext monitorContext,
        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);

        try {
            this.haProxyStatsUrl = new URL(sensorConfiguration.getValue(HAPROXY_STATS_URL_CONFIG)
                .orElseThrow(() -> new SensorInitializationException(String.format(
                    "Could not retrieve HAProxy url from sensor configuration, required property %s.",
                    HAPROXY_STATS_URL_CONFIG))));
        } catch (MalformedURLException e) {
            throw new SensorInitializationException(
                "Url provided for HaProxy stats page is malformed", e);
        }

        final Optional<String> username = sensorConfiguration.getValue(HAPROXY_STATS_AUTH_USERNAME);
        final Optional<String> password = sensorConfiguration.getValue(HAPROXY_STATS_AUTH_PASSWORD);

        if (username.isPresent() ^ password.isPresent()) {
            throw new SensorInitializationException(
                "If you provide a username or a password, you need to provide both.");
        }

        if (username.isPresent() && password.isPresent()) {
            String userAndPassword = username.get() + ":" + password.get();
            authentication = Optional
                .of("Basic " + new String(Base64.getEncoder().encode(userAndPassword.getBytes())));
        } else {
            authentication = Optional.empty();
        }


    }

    @Override protected Measurement measure() throws MeasurementNotAvailableException {

        try {
            URLConnection urlConnection = haProxyStatsUrl.openConnection();
            if (authentication.isPresent()) {
                urlConnection.setRequestProperty("Authorization", authentication.get());
            }

            csvFormat
                .parse(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())));

        } catch (IOException e) {
            throw new MeasurementNotAvailableException(e);
        }

        return measureMentBuilder().now().value(0L).build();
    }

}
