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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import de.uniulm.omi.cloudiator.visor.monitoring.DataSinkImpl.DataSinkConfigurationImpl;
import java.util.Collections;
import org.junit.Test;

public class DataSinkImplTest {

  @Test
  public void equals() {

    final DataSink withoutConfiguration = new DataSinkImpl("test", null);
    final DataSink withConfiguration = new DataSinkImpl("test",
        new DataSinkConfigurationImpl(Collections.singletonMap("key", "value")));
    final DataSink withDifferentConfiguration = new DataSinkImpl("test",
        new DataSinkConfigurationImpl(Collections.singletonMap("different", "config")));
    final DataSink withSameConfiguration = new DataSinkImpl("test",
        new DataSinkConfigurationImpl(Collections.singletonMap("key", "value")));

    assertThat(withoutConfiguration, not(equalTo(withConfiguration)));
    assertThat(withConfiguration, not(equalTo(withDifferentConfiguration)));
    assertThat(withConfiguration, (equalTo(withSameConfiguration)));
    
  }
}
