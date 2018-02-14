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

package de.uniulm.omi.cloudiator.visor;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.visor.exceptions.MonitorException;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitoringService;
import de.uniulm.omi.cloudiator.visor.rest.entities.MonitorDto;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class YamlParser
 * <p>
 * Parses a yaml file present at the {@link YamlParser#CONF_INIT_YAML} location.
 * <p>
 * The monitors in this file will be added to the {@link MonitoringService}.
 * <p>
 * An example can be found in the conf folder.
 */
public class YamlParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(YamlParser.class);
  private static final String CONF_INIT_YAML = "conf/init.yaml";

  /**
   * Parses the file and adds the described monitors to the passed monitoringService.
   *
   * @param monitoringService the monitoringService used to start the monitors.
   * @throws NullPointerException if monitoringService is null
   */
  @Inject
  public YamlParser(MonitoringService monitoringService) {

    checkNotNull(monitoringService, "monitoringService is null");

    LOGGER.info(String.format("Starting initialization using YAML file (%s)", CONF_INIT_YAML));

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    File file = new File(CONF_INIT_YAML);
    if (!file.exists()) {
      LOGGER.warn(String.format("Skipping YAML initialization as file (%s) does not exist.",
          file.getAbsolutePath()));
      return;
    }
    try {
      List<MonitorDto> list = mapper.readValue(file, new TypeReference<List<MonitorDto>>() {
      });

      LOGGER
          .debug(String.format("YAML initialization found the following monitors: %s", list));

      list.forEach(monitorDto -> {
        try {
          LOGGER.debug(String
              .format("YAML initialization is starting the following monitor: %s",
                  monitorDto));
          monitorDto.start(UUID.randomUUID().toString(), monitoringService);
        } catch (MonitorException e) {
          LOGGER.error(String
              .format("YAML initialization could not start monitoring of monitor %s.",
                  monitorDto), e);
        }
      });

    } catch (IOException e) {
      LOGGER.error("YAML initialization could not parse YAML file.", e);
    }

    LOGGER.info("YAML initialization finished successfully.");
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }

}
