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


import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 18.12.14.
 */
public class MeasurementImpl implements Measurement {

    private final long timestamp;
    private final Object value;

    public MeasurementImpl(long timestamp, Object value) {
        checkNotNull(value);
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override public long getTimestamp() {
        return timestamp;
    }

    @Override public Object getValue() {
        return value;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MeasurementImpl that = (MeasurementImpl) o;

        if (getTimestamp() != that.getTimestamp())
            return false;
        return getValue().equals(that.getValue());

    }

    @Override public int hashCode() {
        int result = (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        result = 31 * result + getValue().hashCode();
        return result;
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this).add("timestamp",timestamp).add("value",value).toString();
    }
}
