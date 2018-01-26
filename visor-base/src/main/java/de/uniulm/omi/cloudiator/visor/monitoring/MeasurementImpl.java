/*
 * Copyright (c) 2014-2015 University of Ulm
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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * Created by daniel on 18.12.14.
 */
public class MeasurementImpl<E> implements Measurement<E> {

  private final long timestamp;
  private final E value;
  private final Map<String, String> tags;

  MeasurementImpl(long timestamp, E value, Map<String, String> tags) {
    checkNotNull(timestamp);
    this.timestamp = timestamp;
    checkNotNull(value);
    this.value = value;
    checkNotNull(tags);
    this.tags = tags;
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public E getValue() {
    return value;
  }

  @Override
  public Map<String, String> tags() {
    return ImmutableMap.copyOf(tags);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MeasurementImpl that = (MeasurementImpl) o;

    return getTimestamp() == that.getTimestamp() && getValue().equals(that.getValue());

  }

  @Override
  public int hashCode() {
    int result = (int) (getTimestamp() ^ (getTimestamp() >>> 32));
    result = 31 * result + getValue().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("timestamp", timestamp).add("value", value)
        .add("tags", tags).toString();
  }
}
