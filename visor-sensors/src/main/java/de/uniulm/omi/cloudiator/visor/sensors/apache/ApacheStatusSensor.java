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

package de.uniulm.omi.cloudiator.visor.sensors.apache;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.*;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.*;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by daniel on 11.02.16.
 */
public class ApacheStatusSensor extends AbstractSensor {

    private static class StatusMetric implements MeasurableMetric {

        private MeasurableMetric measurableMetric;

        private StatusMetric(MeasurableMetric measurableMetric) {
            this.measurableMetric = measurableMetric;
        }

        private static StatusMetric valueOf(String string) {

            try {
                return new StatusMetric(RawMetric.valueOf(string));
            } catch (IllegalArgumentException ignored) {
            }

            try {
                return new StatusMetric(DerivedMetric.valueOf(string));
            } catch (IllegalArgumentException ignored) {
            }

            throw new IllegalArgumentException("Could not find the metric " + string);
        }

        @Override public Measurement measure(Map<RawMetric, Measurement> currentMeasurements,
            Map<RawMetric, Measurement> pastMeasurements) throws MeasurementNotAvailableException {
            return measurableMetric.measure(currentMeasurements, pastMeasurements);
        }
    }


    private enum DerivedMetric implements MeasurableMetric {

        CURRENT_REQ_PER_SEC {
            @Override public Measurement measure(Map<RawMetric, Measurement> currentMeasurements,
                Map<RawMetric, Measurement> pastMeasurements)
                throws MeasurementNotAvailableException {

                Measurement newAccess = currentMeasurements.get(RawMetric.TOTAL_ACCESS);

                if (!pastMeasurements.containsKey(RawMetric.TOTAL_ACCESS)) {
                    throw new MeasurementNotAvailableException(
                        "No old value available, skipping measurement for " + this);
                }

                Measurement oldAccess = pastMeasurements.get(RawMetric.TOTAL_ACCESS);
                long valueDifference =
                    ((Long) newAccess.getValue()) - ((Long) oldAccess.getValue());
                long timeDifferenceInSec = TimeUnit.SECONDS
                    .convert(newAccess.getTimestamp() - oldAccess.getTimestamp(),
                        TimeUnit.MILLISECONDS);
                return MeasurementBuilder.newBuilder().timestamp(newAccess.getTimestamp())
                    .value((double) valueDifference / timeDifferenceInSec).build();

            }
        },
        CURRENT_KB_PER_SEC {
            @Override public Measurement measure(Map<RawMetric, Measurement> currentMeasurements,
                Map<RawMetric, Measurement> pastMeasurements)
                throws MeasurementNotAvailableException {

                Measurement newAccess = currentMeasurements.get(RawMetric.TOTAL_KB);

                if (!pastMeasurements.containsKey(RawMetric.TOTAL_KB)) {
                    throw new MeasurementNotAvailableException(
                        "No old value available, skipping measurement for " + this);
                }

                Measurement oldAccess = pastMeasurements.get(RawMetric.TOTAL_KB);
                long valueDifference =
                    ((Long) newAccess.getValue()) - ((Long) oldAccess.getValue());
                long timeDifferenceInSec = TimeUnit.SECONDS
                    .convert(newAccess.getTimestamp() - oldAccess.getTimestamp(),
                        TimeUnit.MILLISECONDS);
                return MeasurementBuilder.newBuilder().timestamp(newAccess.getTimestamp())
                    .value((double) valueDifference / timeDifferenceInSec).build();

            }
        };
    }


    private enum RawMetric implements ParsableMetric, MeasurableMetric {

        TOTAL_ACCESS {
            @Override public String statusString() {
                return "Total Accesses";
            }

            @Override public Object toType(String string) {
                return Long.valueOf(string);
            }
        },
        TOTAL_KB {
            @Override public String statusString() {
                return "Total kBytes";
            }

            @Override public Object toType(String string) {
                return Long.valueOf(string);
            }
        },
        CPU_LOAD {
            @Override public String statusString() {
                return "CPULoad";
            }

            @Override public Object toType(String string) {
                return Double.valueOf(string);
            }


        },
        UPTIME {
            @Override public String statusString() {
                return "Uptime";
            }

            @Override public Object toType(String string) {
                return Long.valueOf(string);
            }


        },
        REQ_PER_SEC {
            @Override public String statusString() {
                return "ReqPerSec";
            }

            @Override public Object toType(String string) {
                return Double.valueOf(string);
            }
        },
        BYTES_PER_SEC {
            @Override public String statusString() {
                return "BytesPerSec";
            }

            @Override public Object toType(String string) {
                return Double.valueOf(string);
            }
        },
        BYTS_PER_REQ {
            @Override public String statusString() {
                return "BytesPerReq";
            }

            @Override public Object toType(String string) {
                return Double.valueOf(string);
            }
        },
        BUSY_WORKERS {
            @Override public String statusString() {
                return "BusyWorkers";
            }

            @Override public Object toType(String string) {
                return Long.valueOf(string);
            }

        },
        IDLE_WORKERS {
            @Override public String statusString() {
                return "IdleWorkers";
            }

            @Override public Object toType(String string) {
                return Long.valueOf(string);
            }
        },
        SCOREBOARD {
            @Override public String statusString() {
                return "Scoreboard";
            }

            @Override public Object toType(String string) {
                return string;
            }
        };

        @Override public Measurement measure(Map<RawMetric, Measurement> currentMeasurements,
            Map<RawMetric, Measurement> pastMeasurements) throws MeasurementNotAvailableException {
            return currentMeasurements.get(this);
        }

        static private RawMetric of(String statusString) {
            for (RawMetric rawMetric : RawMetric.values()) {
                if (rawMetric.statusString().equals(statusString)) {
                    return rawMetric;
                }
            }
            throw new IllegalArgumentException(
                "No metric present for status string " + statusString);
        }

    }


    private interface ParsableMetric {
        String statusString();

        Object toType(String string);
    }


    private interface MeasurableMetric {
        Measurement measure(Map<RawMetric, Measurement> currentMeasurements,
            Map<RawMetric, Measurement> pastMeasurement) throws MeasurementNotAvailableException;
    }


    private static final String URL_CONFIG = "apache.status.url";
    private static final String URL_DEFAULT = "http://localhost:80/server-status?auto";
    private static final String MONITORED_METRIC_CONFIG = "apache.status.metric";
    private static Map<URL, Map<RawMetric, Measurement>> oldMeasurements =
        new ConcurrentHashMap<>();
    private static LoadingCache<URL, Map<RawMetric, Measurement>> rawMeasurementCache;

    private StatusMetric statusMetric;
    private URL url;

    static {
        rawMeasurementCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<URL, Map<RawMetric, Measurement>>() {
                @Override public void onRemoval(
                    RemovalNotification<URL, Map<RawMetric, Measurement>> notification) {
                    oldMeasurements.put(notification.getKey(), notification.getValue());
                }
            }).build(new CacheLoader<URL, Map<RawMetric, Measurement>>() {
                @Override public Map<RawMetric, Measurement> load(@Nullable URL key)
                    throws Exception {
                    checkNotNull(key);
                    return new RawMetricMeasurementSupplier(key).get();
                }
            });
    }


    @Override protected void initialize(MonitorContext monitorContext,
        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);
        try {
            this.url = new URL(sensorConfiguration.getValue(URL_CONFIG).orElse(URL_DEFAULT));
        } catch (MalformedURLException e) {
            throw new SensorInitializationException(
                "Url provided for apache status page is malformed.", e);
        }

        try {
            statusMetric = StatusMetric.valueOf(
                sensorConfiguration.getValue(MONITORED_METRIC_CONFIG).orElseThrow(
                    () -> new SensorInitializationException(
                        "Mandatory configuration value " + MONITORED_METRIC_CONFIG
                            + "was not supplied.")));
        } catch (IllegalArgumentException e) {
            throw new SensorInitializationException(e);
        }
    }

    @Override protected Measurement measure() throws MeasurementNotAvailableException {
        try {
            Map<RawMetric, Measurement> pastMeasurements = oldMeasurements.get(url);
            if (pastMeasurements == null) {
                pastMeasurements = Collections.emptyMap();
            }
            return statusMetric.measure(rawMeasurementCache.get(url), pastMeasurements);
        } catch (ExecutionException e) {
            throw new MeasurementNotAvailableException(e);
        }
    }


    /**
     * Supplies measurements from the given url.
     */
    private static class RawMetricMeasurementSupplier
        implements Supplier<Map<RawMetric, Measurement>> {

        private final URL url;
        private final StatusLineParser statusLineParser;

        private RawMetricMeasurementSupplier(URL url) {
            checkNotNull(url);
            this.url = url;
            this.statusLineParser = new StatusLineParser();
        }

        @Override public Map<RawMetric, Measurement> get() {
            Map<RawMetric, Measurement> result = new HashMap<>(RawMetric.values().length);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                long now = System.currentTimeMillis();
                String line;
                while ((line = reader.readLine()) != null) {
                    final MetricValue metricValue = statusLineParser.apply(line);
                    if (metricValue != null) {
                        result.put(metricValue.getRawMetric(),
                            MeasurementBuilder.newBuilder().timestamp(now)
                                .value(metricValue.getValue()).build());
                    }
                }
                return result;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }


    /**
     * Represents a simple metric value.
     */
    private static class MetricValue {

        private final RawMetric rawMetric;
        private final Object value;

        private static MetricValue of(RawMetric rawMetric, Object value) {
            return new MetricValue(rawMetric, value);
        }

        private MetricValue(RawMetric rawMetric, Object value) {
            this.rawMetric = rawMetric;
            this.value = value;
        }

        public RawMetric getRawMetric() {
            return rawMetric;
        }

        public Object getValue() {
            return value;
        }
    }


    /**
     * Parses a single line of the status page output and returns the metric and
     * the associated value.
     */
    private static class StatusLineParser implements Function<String, MetricValue> {

        private static final String SEPARATOR = ":";

        @Nullable @Override public MetricValue apply(@Nullable String input) {
            checkNotNull(input);
            final String[] values = input.split(SEPARATOR);
            checkState(values.length == 2);
            try {
                final RawMetric rawMetric = RawMetric.of(values[0].trim());
                return MetricValue.of(rawMetric, rawMetric.toType(values[1].trim()));
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Could not parse metric value.", e);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
