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

package de.uniulm.omi.cloudiator.visor.monitoring;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public class DataSinkImpl implements DataSink {

  private final String type;
  @JsonSerialize(as = DataSinkConfigurationImpl.class)
  @JsonDeserialize(as = DataSinkConfigurationImpl.class)
  private final DataSinkConfiguration config;

  @JsonCreator
  public DataSinkImpl(@JsonProperty("type") String type,
      @Nullable @JsonProperty("config") DataSinkConfiguration config) {

    checkNotNull(type, "type is null");
    this.type = type;
    if (config == null) {
      this.config = new DataSinkConfigurationImpl(null);
    } else {
      this.config = config;
    }
  }

  public static class DataSinkConfigurationImpl implements DataSinkConfiguration {

    private final Map<String, String> values;

    @JsonCreator
    public DataSinkConfigurationImpl(
        @Nullable @JsonProperty("values") Map<String, String> values) {
      if (values == null) {
        this.values = new HashMap<>();
      } else {
        this.values = values;
      }
    }

    @Override
    @JsonProperty("values")
    public Map<String, String> values() {
      return ImmutableMap.copyOf(values);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      DataSinkConfigurationImpl that = (DataSinkConfigurationImpl) o;
      return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {

      return Objects.hash(values);
    }
  }

  @Override
  @JsonProperty("type")
  public String type() {
    return type;
  }

  @Override
  @JsonProperty("config")
  public DataSinkConfiguration config() {
    return config;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataSinkImpl dataSink = (DataSinkImpl) o;
    return Objects.equals(type, dataSink.type) &&
        Objects.equals(config, dataSink.config);
  }

  @Override
  public int hashCode() {

    return Objects.hash(type, config);
  }
}
