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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.*;
import de.uniulm.omi.cloudiator.visor.util.MeasurementDifference;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 20.01.16.
 */
public class HaProxyStatsPageSensor extends AbstractSensor<Object> {


    private static class Measurables {

        public static Measureable of(String string) {
            try {
                return RawMetric.valueOf(string);
            } catch (IllegalArgumentException ignored) {
            }

            try {
                return CompositeMetric.valueOf(string);
            } catch (IllegalArgumentException ignored) {

            }

            throw new IllegalArgumentException(
                String.format("Could not find metric with name %s", string));
        }
    }


    private enum CompositeMetric implements Measureable {

        SESSION_PER_SECOND {
            @Override public Measurement<Object> measure(Map<RawMetric, Measurement<Object>> old,
                Map<RawMetric, Measurement<Object>> current)
                throws MeasurementNotAvailableException {


                Measurement currentMeasurement = current.get(RawMetric.SESSION_TOTAL);
                Measurement oldMeasurement = old.get(RawMetric.SESSION_TOTAL);

                if (oldMeasurement == null) {
                    throw new MeasurementNotAvailableException(
                        "Measurement not available as old values is missing.");
                }
                if (currentMeasurement == null) {
                    throw new MeasurementNotAvailableException("No current value available.");
                }

                return MeasurementBuilder.newBuilder().timestamp(currentMeasurement.getTimestamp())
                    .value(MeasurementDifference.of(oldMeasurement, currentMeasurement)
                        .timeDifference(1, TimeUnit.SECONDS)).build();
            }

        }, TWO_XX_PER_SECOND {
            @Override public Measurement<Object> measure(Map<RawMetric, Measurement<Object>> old,
                Map<RawMetric, Measurement<Object>> current)
                throws MeasurementNotAvailableException {

                Measurement currentMeasurement = current.get(RawMetric.TWO_XX);
                Measurement oldMeasurement = old.get(RawMetric.TWO_XX);

                if (oldMeasurement == null) {
                    throw new MeasurementNotAvailableException(
                        "Measurement not available as old values is missing.");
                }
                if (currentMeasurement == null) {
                    throw new MeasurementNotAvailableException("No current value available.");
                }

                return MeasurementBuilder.newBuilder().timestamp(currentMeasurement.getTimestamp())
                    .value(MeasurementDifference.of(oldMeasurement, currentMeasurement)
                        .timeDifference(1, TimeUnit.SECONDS)).build();
            }
        }
    }


    private enum RawMetric implements BaseHAProxyMetric, Measureable {

        SESSION_TOTAL {
            @Override public String string() {
                return "stot";
            }

            @Override public Object toType(String value) {
                return Long.valueOf(value);
            }
        }, TWO_XX {
            @Override public String string() {
                return "hrsp_2xx";
            }

            @Override public Object toType(String value) {
                return Long.valueOf(value);
            }
        };

        @Override public Measurement<Object> measure(Map<RawMetric, Measurement<Object>> old,
            Map<RawMetric, Measurement<Object>> current) {
            return current.get(this);
        }
    }


    private interface BaseHAProxyMetric {
        String string();

        Object toType(String value);
    }


    private interface Measureable {
        Measurement<Object> measure(Map<RawMetric, Measurement<Object>> old,
            Map<RawMetric, Measurement<Object>> current) throws MeasurementNotAvailableException;
    }


    private final static String HAPROXY_STATS_URL_CONFIG = "haproxy.url";
    private final static String HAPROXY_STATS_AUTH_USERNAME = "haproxy.auth.username";
    private final static String HAPROXY_STATS_AUTH_PASSWORD = "haproxy.auth.password";
    private final static String HAPROXY_STATS_GROUP = "haproxy.group";
    private final static String HAPROXY_STATS_GROUP_DEFAULT = "http-in";
    private final static String HAPROXY_METRIC = "haproxy.metric";
    private URL haProxyStatsUrl;
    private String haProxyGroup;
    @Nullable private String username;
    @Nullable private String password;
    private Measureable metric;

    private Table<URL, String, Map<RawMetric, Measurement<Object>>> old = HashBasedTable.create();
    private Table<URL, String, Map<RawMetric, Measurement<Object>>> current =
        HashBasedTable.create();

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

        username = sensorConfiguration.getValue(HAPROXY_STATS_AUTH_USERNAME).orElse(null);
        password = sensorConfiguration.getValue(HAPROXY_STATS_AUTH_PASSWORD).orElse(null);

        if (username == null ^ password == null) {
            throw new SensorInitializationException(
                "If you provide a username or a password, you need to provide both.");
        }

        haProxyGroup =
            sensorConfiguration.getValue(HAPROXY_STATS_GROUP).orElse(HAPROXY_STATS_GROUP_DEFAULT);

        try {
            this.metric = Measurables.of(sensorConfiguration.getValue(HAPROXY_METRIC).orElseThrow(
                () -> new SensorInitializationException(
                    "Configuration parameter " + HAPROXY_METRIC + " is required")));
        } catch (IllegalArgumentException e) {
            throw new SensorInitializationException(e);
        }


    }

    @Override protected Measurement<Object> measureSingle() throws MeasurementNotAvailableException {

        try {

            URLConnection urlConnection;
            if (username != null && password != null) {
                urlConnection = URLConnectionFactory.of(haProxyStatsUrl, username, password);
            } else {
                urlConnection = URLConnectionFactory.of(haProxyStatsUrl);
            }

            CSVParser csvParser = CSVParserFactory.of(urlConnection);

            if (current.contains(haProxyStatsUrl, haProxyGroup)) {
                old.put(haProxyStatsUrl, haProxyGroup, current.get(haProxyStatsUrl, haProxyGroup));
            } else {
                old.put(haProxyStatsUrl, haProxyGroup, Collections.emptyMap());
            }
            current.put(haProxyStatsUrl, haProxyGroup,
                new RawMetricForUrlSupplier(csvParser, haProxyGroup).get());

            return metric.measure(old.get(haProxyStatsUrl, haProxyGroup),
                current.get(haProxyStatsUrl, haProxyGroup));

        } catch (IOException e) {
            throw new MeasurementNotAvailableException(e);
        }
    }

    private static class URLConnectionFactory {

        private static URLConnection of(URL url) throws IOException {
            checkNotNull(url);
            return url.openConnection();
        }

        private static URLConnection of(URL url, String username, String password)
            throws IOException {
            checkNotNull(url);
            checkNotNull(username);
            checkNotNull(password);

            String userAndPassword = username + ":" + password;
            String authentication =
                "Basic " + new String(Base64.getEncoder().encode(userAndPassword.getBytes()));
            final URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Authorization", authentication);
            return urlConnection;
        }

    }


    private static class CSVParserFactory {

        private final static CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader();

        private static CSVParser of(URLConnection urlConnection) throws IOException {
            return csvFormat
                .parse(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())));
        }

    }


    private static class RawMetricForUrlSupplier
        implements Supplier<Map<RawMetric, Measurement<Object>>> {

        private final CSVParser csvParser;
        private final String haProxyGroup;

        private RawMetricForUrlSupplier(CSVParser csvParser, String haProxyGroup) {
            this.csvParser = csvParser;
            this.haProxyGroup = haProxyGroup;
        }

        @Override public Map<RawMetric, Measurement<Object>> get() {
            Map<RawMetric, Measurement<Object>> measurements = new HashMap<>(RawMetric.values().length);
            for (CSVRecord csvRecord : csvParser) {
                if (csvRecord.get("# pxname").equals(haProxyGroup)) {
                    for (RawMetric rawMetric : RawMetric.values()) {
                        if (csvRecord.isMapped(rawMetric.string())) {
                            measurements.put(rawMetric, MeasurementBuilder.newBuilder().now()
                                .value(rawMetric.toType(csvRecord.get(rawMetric.string())))
                                .build());
                        }
                    }
                }
            }
            return ImmutableMap.copyOf(measurements);
        }
    }

}
