/*
 *
 *  * Copyright (c) 2014 University of Ulm
 *  *
 *  * See the NOTICE file distributed with this work for additional information
 *  * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package de.uniulm.omi.executionware.agent.monitoring.metric.impl;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class ServerMetricTest {

    private final String testName = "MyTestName";
    private final long testTimeStamp = System.currentTimeMillis();
    private final Object testValue = "532532";
    private final String testIp = "127.0.0.1";

    private ServerMetric testServerMetric;

    @Before
    public void before() {
        this.testServerMetric = new ServerMetric(this.testName, this.testValue, this.testTimeStamp, this.testIp);
    }

    @Test(expected = NullPointerException.class)
    public void testIpNotNull() {
        //noinspection UnusedDeclaration
        final ServerMetric serverMetric = new ServerMetric(this.testName, this.testValue, this.testTimeStamp, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIpNotEmpty() {
        //noinspection UnusedDeclaration
        final ServerMetric serverMetric = new ServerMetric(this.testName, this.testValue, this.testTimeStamp, "");
    }

    @Test(expected = NullPointerException.class)
    public void testValueNotNull() {
        //noinspection UnusedDeclaration
        final ServerMetric serverMetric = new ServerMetric(this.testName, null, this.testTimeStamp, this.testIp);
    }

    @Test(expected = NullPointerException.class)
    public void testNameNotNull() {
        //noinspection UnusedDeclaration
        final ServerMetric serverMetric = new ServerMetric(null, this.testValue, this.testTimeStamp, this.testIp);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTimeStampNotNegativeOr0() {
        //noinspection UnusedDeclaration
        final ServerMetric serverMetricNegative = new ServerMetric(this.testName, this.testValue, -1, this.testIp);
        //noinspection UnusedDeclaration
        final ServerMetric serverMetric0 = new ServerMetric(this.testName, this.testValue, 0, this.testIp);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameNotEmpty() {
        //noinspection UnusedDeclaration
        final ServerMetric serverMetric = new ServerMetric("", this.testValue, this.testTimeStamp, this.testIp);
    }


    @Test
    public void testGetIp() throws Exception {
        assertThat(this.testServerMetric.getIp(), equalTo(this.testIp));
    }

    @Test
    public void testGetName() throws Exception {
        assertThat(this.testServerMetric.getName(), equalTo(this.testName));
    }

    @Test
    public void testGetValue() throws Exception {
        assertThat(this.testServerMetric.getValue(), equalTo(this.testValue));
    }

    @Test
    public void testGetTimestamp() throws Exception {
        assertThat(this.testServerMetric.getTimestamp(), equalTo(this.testTimeStamp));
    }

    @Test
    public void testToString() throws Exception {
        String toString = this.testServerMetric.toString();
        assertThat(toString, containsString(this.testName));
        assertThat(toString, containsString(String.valueOf(this.testTimeStamp)));
        assertThat(toString, containsString((String) this.testValue));
        assertThat(toString, containsString(this.testIp));
    }
}