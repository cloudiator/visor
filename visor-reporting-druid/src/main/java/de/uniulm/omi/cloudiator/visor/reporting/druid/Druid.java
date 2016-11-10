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

package de.uniulm.omi.cloudiator.visor.reporting.druid;

import com.metamx.tranquility.config.DataSourceConfig;
import com.metamx.tranquility.config.PropertiesBasedConfig;
import com.metamx.tranquility.config.TranquilityConfig;
import com.metamx.tranquility.druid.DruidBeams;
import com.metamx.tranquility.tranquilizer.Tranquilizer;
import com.twitter.util.FutureEventListener;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import org.apache.logging.log4j.LogManager;
import scala.runtime.BoxedUnit;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by daniel on 07.09.16.
 */
public class Druid implements ReportingInterface<Metric> {

    private final Function<Metric, Map<String, Object>> converter;
    private final DataSourceConfig<PropertiesBasedConfig> visorConfig;

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Druid.class);

    public Druid() {
        this.converter = new DruidMetricConverter();
        final InputStream configStream =
            Druid.class.getClassLoader().getResourceAsStream("visor.json");
        TranquilityConfig<PropertiesBasedConfig> config = TranquilityConfig.read(configStream);
        visorConfig = config.getDataSource("visor");
    }


    @Override public void report(Metric item) throws ReportingException {
        this.report(Collections.singleton(item));
    }

    @Override public synchronized void report(Collection<Metric> items) throws ReportingException {
        Tranquilizer<Map<String, Object>> sender =
            DruidBeams.fromConfig(visorConfig).buildTranquilizer(visorConfig.tranquilizerBuilder());
        sender.start();
        try {
            items.stream().map(converter).forEach(new Consumer<Map<String, Object>>() {

                @Override public void accept(Map<String, Object> message) {
                    sender.send(message).addEventListener(new FutureEventListener<BoxedUnit>() {


                        @Override public void onSuccess(BoxedUnit value) {
                            System.out.println("Sent message! " + message);
                        }

                        @Override public void onFailure(Throwable cause) {
                            System.err.println("Failed sending message " + message);
                            cause.printStackTrace();
                        }


                    });
                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sender.flush();
            sender.close();
        }
    }


    /**
     @Override public synchronized void report(Collection<Metric> items) throws ReportingException {
     // Read config from "example.json" on the classpath.
     final InputStream configStream =
     Druid.class.getClassLoader().getResourceAsStream("example.json");
     final TranquilityConfig<PropertiesBasedConfig> config =
     TranquilityConfig.read(configStream);
     final DataSourceConfig<PropertiesBasedConfig> wikipediaConfig =
     config.getDataSource("wikipedia");
     final Tranquilizer<Map<String, Object>> sender = DruidBeams.fromConfig(wikipediaConfig)
     .buildTranquilizer(wikipediaConfig.tranquilizerBuilder());

     sender.start();

     try {
     // Send 10000 objects

     for (int i = 0; i < 10000; i++) {
     // Build a sample event to send; make sure we use a current date
     final Map<String, Object> obj =
     ImmutableMap.<String, Object>of("timestamp", new DateTime().toString(), "page",
     "foo", "added", i);

     // Asynchronously send event to Druid:
     sender.send(obj).addEventListener(new FutureEventListener<BoxedUnit>() {
     @Override public void onSuccess(BoxedUnit value) {
     LOGGER.info("Sent message: " + obj);
     }

     @Override public void onFailure(Throwable e) {
     if (e instanceof MessageDroppedException) {
     LOGGER.warn("Dropped message: " + obj, e);
     } else {
     LOGGER.error("Failed to send message: " + obj, e);
     }
     }
     });
     }
     } finally {
     sender.flush();
     sender.stop();
     }
     }
     **/
}
