/*
 * Copyright (c) 2014-2018 University of Ulm
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

package de.uniulm.omi.cloudiator.visor.reporting.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import java.util.function.Function;

public class JacksonJsonEncoding<T> implements JMSEncoding {

  private static final ObjectMapper om = new ObjectMapper();
  private final Function<Metric, T> converter;

  private JacksonJsonEncoding(
      Function<Metric, T> converter) {
    this.converter = converter;
  }

  public static <T> JacksonJsonEncoding<T> of(Function<Metric, T> converter) {
    return new JacksonJsonEncoding<>(converter);
  }

  @Override
  public String encode(Metric metric) throws EncodingException {
    try {
      return om.writeValueAsString(converter.apply(metric));
    } catch (JsonProcessingException e) {
      throw new EncodingException(
          String.format("Could not encode metric %s. Error was %s", metric, e.getMessage()), e);
    }
  }
}
