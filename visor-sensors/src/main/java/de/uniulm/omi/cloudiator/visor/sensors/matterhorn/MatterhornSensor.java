/*
 * Copyright (c) 2014-2017 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.sensors.matterhorn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.AbstractSensor;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.opencastproject.security.api.TrustedHttpClient;
import org.opencastproject.security.util.StandAloneTrustedHttpClientImpl;
import org.opencastproject.util.data.Option;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 26.01.17.
 */
public class MatterhornSensor extends AbstractSensor<Integer> {

    private interface ValueAccess {
        int getValue(Entry entry);
    }


    private enum EntryType implements ValueAccess {
        TOTAL {
            @Override public int getValue(Entry entry) {
                return entry.getTotal();
            }
        }, OFFSET {
            @Override public int getValue(Entry entry) {
                return entry.getOffset();
            }
        }, COUNT {
            @Override public int getValue(Entry entry) {
                return entry.getCount();
            }
        }, LIMIT {
            @Override public int getValue(Entry entry) {
                return entry.getLimit();
            }
        }
    }


    private final static String URL_CONFIGURATION_KEY = "matterhorn.url";
    private final static String URL_DEFAULT_VALUE =
        "http://localhost:8080/admin-ng/event/events.json?filter=status:EVENTS.EVENTS.STATUS.PROCESSING";
    private final static String USER_CONFIGURATION_KEY = "matterhorn.user";
    private final static String USER_DEFAULT_VALUE = "opencast_system_account";
    private final static String PASSWORD_CONFIGURATION_KEY = "matterhorn.password";
    private final static String PASSWORD_DEFAULT_VALUE = "CHANGE_ME";
    private final static String ENTRY_CONFIGURATION_KEY = "matterhorn.entry";
    private final static String ENTRY_DEFAULT_VALUE = EntryType.TOTAL.name();

    private String url;
    private String user;
    private String password;
    private EntryType entryType;

    @Override protected void initialize(MonitorContext monitorContext,
        SensorConfiguration sensorConfiguration) throws SensorInitializationException {
        super.initialize(monitorContext, sensorConfiguration);

        this.url = sensorConfiguration.getValue(URL_CONFIGURATION_KEY).orElse(URL_DEFAULT_VALUE);
        this.user = sensorConfiguration.getValue(USER_CONFIGURATION_KEY).orElse(USER_DEFAULT_VALUE);
        this.password =
            sensorConfiguration.getValue(PASSWORD_CONFIGURATION_KEY).orElse(PASSWORD_DEFAULT_VALUE);
        try {
            this.entryType = EntryType.valueOf(
                sensorConfiguration.getValue(ENTRY_CONFIGURATION_KEY).orElse(ENTRY_DEFAULT_VALUE));
        } catch (IllegalArgumentException e) {
            throw new SensorInitializationException(String
                .format("Illegal value %s configured for configuration key %s.",
                    sensorConfiguration.getValue(ENTRY_CONFIGURATION_KEY), ENTRY_CONFIGURATION_KEY),
                e);
        }
    }

    @Override protected Measurement<Integer> measureSingle()
        throws MeasurementNotAvailableException {
        try {
            final String response = new MatterhornClient(user, password).request(url);
            final Entry entry = ResponseParser.parse(response);
            return measurementBuilder(Integer.class).now().value(entryType.getValue(entry)).build();
        } catch (IOException e) {
            throw new MeasurementNotAvailableException(
                String.format("%s failed to take measurement.", this), e);
        }
    }

    private final static class ResponseParser {

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        static Entry parse(String response) throws IOException {
            return OBJECT_MAPPER.readValue(response, Entry.class);
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true) private final static class Entry {

        private int total;
        private int offset;
        private int count;
        private int limit;

        Entry() {

        }

        Entry(int total, int offset, int count, int limit) {
            this.total = total;
            this.offset = offset;
            this.count = count;
            this.limit = limit;
        }

        public int getTotal() {
            return total;
        }

        public int getOffset() {
            return offset;
        }

        public int getCount() {
            return count;
        }

        public int getLimit() {
            return limit;
        }

        @Override public String toString() {
            return "Entry{" + "total=" + total + ", offset=" + offset + ", count=" + count
                + ", limit=" + limit + '}';
        }
    }


    private final static class MatterhornClient {

        private final TrustedHttpClient httpClient;

        private MatterhornClient(String user, String pass) {
            checkNotNull(user, "user is null");
            checkArgument(!user.isEmpty(), "user is empty");
            checkNotNull(pass, "pass is null");
            checkArgument(!pass.isEmpty(), "pass is empty");

            this.httpClient =
                new StandAloneTrustedHttpClientImpl(user, pass, Option.none(), Option.none(),
                    Option.none());
        }

        String request(String url) throws IOException {
            HttpGet httpget = new HttpGet(url);
            final HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, Charsets.UTF_8);
        }

    }
}
