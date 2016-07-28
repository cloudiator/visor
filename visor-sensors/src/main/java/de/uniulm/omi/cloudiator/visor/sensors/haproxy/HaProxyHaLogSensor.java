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
import de.uniulm.omi.cloudiator.visor.monitoring.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.exec.*;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;



/**
 * Created by daniel on 26.07.16.
 */
public class HaProxyHaLogSensor extends AbstractSensor {

    private final static String LOG_LOCATION_FIELD = "logPath";
    private final static String LOG_LOCATION_DEFAULT_VALUE = "/var/log/haproxy.log";
    private final static String METRIC_FIELD = "metric";
    private final static String METRIC_DEFAULT_VALUE = RawMetric.REQUEST_COUNT.toString();

    private String logLocation;
    private BaseHAProxyMetric metric;

    @Override protected void initialize(MonitorContext monitorContext,
        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);
        this.logLocation =
            sensorConfiguration.getValue(LOG_LOCATION_FIELD).orElse(LOG_LOCATION_DEFAULT_VALUE);
        try {
            this.metric = RawMetric
                .valueOf(sensorConfiguration.getValue(METRIC_FIELD).orElse(METRIC_DEFAULT_VALUE));
        } catch (IllegalArgumentException e) {
            throw new SensorInitializationException(e);
        }

    }

    @Override protected Measurement measureSingle() throws MeasurementNotAvailableException {
        return null;
    }

    @Override protected Set<Measurement<Long>> measureSet()
        throws MeasurementNotAvailableException {
        return new HalogParser(this.metric).apply(new Halog(logLocation).measure());
    }

    private enum RawMetric implements BaseHAProxyMetric {

        REQUEST_COUNT {
            @Override public String string() {
                return "#req";
            }
        }, ERROR_COUNT {
            @Override public String string() {
                return "err";
            }

        }, AVERAGE_TOTAL_TIME {
            @Override public String string() {
                return "ttot";
            }

        }, AVERAGE_RESPONSE_TIME {
            @Override public String string() {
                return "tavg";
            }

        }, AVERAGE_TOTAL_TIME_OK {
            @Override public String string() {
                return "oktot";
            }

        }, AVERAGE_RESPONSE_TIME_OK {
            @Override public String string() {
                return "okavg";
            }

        }, AVERAGE_BYTES_RETURNED {
            @Override public String string() {
                return "bavg";
            }

        }, TOTAL_BYTES_RETURNED {
            @Override public String string() {
                return "btot";
            }

        };

        @Override public Long toLong(String value) {
            return Long.valueOf(value);
        }
    }


    private static class CSVParserFactory {
        private final static CSVFormat csvFormat =
            CSVFormat.DEFAULT.withDelimiter(" ".charAt(0)).withHeader();

        private static CSVParser of(String output) throws IOException {
            return csvFormat.parse(new StringReader(output));
        }

    }


    private static class Halog {

        private final CommandLine command;
        private final Path filePath;

        private Halog(String file) {
            command = new CommandLine("halog");
            command.addArgument("-u");
            this.filePath = FileSystems.getDefault().getPath(file);
        }

        String measure() throws MeasurementNotAvailableException {

            try (final InputStream inputStream = Files.newInputStream(filePath);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                OutputStream errorStream = new OutputStream() {
                    @Override public void write(int i) throws IOException {
                        //do nothing
                    }
                }) {

                DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
                ExecuteStreamHandler streamHandler =
                    new PumpStreamHandler(outputStream, errorStream, inputStream);

                ExecuteWatchdog watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
                Executor executor = new DefaultExecutor();
                executor.setExitValue(0);
                executor.setWatchdog(watchdog);
                executor.setStreamHandler(streamHandler);
                executor.execute(command, resultHandler);
                resultHandler.waitFor();
                return outputStream.toString();
            } catch (IOException | InterruptedException e) {
                throw new MeasurementNotAvailableException(e);
            }
        }
    }


    private static class HalogParser implements Function<String, Set<Measurement<Long>>> {

        BaseHAProxyMetric metric;

        HalogParser(BaseHAProxyMetric metric) {
            this.metric = metric;
        }

        @Override public Set<Measurement<Long>> apply(final String s) {
            Set<Measurement<Long>> measurements = new HashSet<>();
            try {
                final CSVParser csvParser = CSVParserFactory.of(s);
                for (CSVRecord csvRecord : csvParser) {
                    System.out.println(csvRecord.toMap());
                    if (csvRecord.isMapped(metric.string())) {
                        checkState(csvRecord.isMapped("src"));
                        measurements.add(MeasurementBuilder.newBuilder(Long.class).now()
                            .value(metric.toLong(csvRecord.get(metric.string())))
                            .addTag("src", csvRecord.get("src")).build());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return measurements;
        }
    }


    private interface BaseHAProxyMetric {
        String string();

        Long toLong(String value);
    }

}
