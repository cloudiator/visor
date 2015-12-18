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

package de.uniulm.omi.cloudiator.visor.rest.resources;

import java.util.List;
import java.util.Set;

/**
 * Created by daniel on 07.04.15.
 */
public class MonitorWithLinks extends LinkWrapper<Monitor> implements MonitorEntity {

    public MonitorWithLinks(Monitor wrappedEntity, Set<Link> links) {
        super(wrappedEntity, links);

    }

    @Override public String getMetricName() {
        return this.wrappedEntity.getMetricName();
    }

    @Override public void setMetricName(String metricName) {
        this.wrappedEntity.setMetricName(metricName);
    }

    @Override public String getSensorClassName() {
        return this.wrappedEntity.getSensorClassName();
    }

    @Override public void setSensorClassName(String sensorClassName) {
        this.wrappedEntity.setSensorClassName(sensorClassName);
    }

    @Override public Interval getInterval() {
        return this.wrappedEntity.getInterval();
    }

    @Override public void setInterval(Interval interval) {
        this.wrappedEntity.setInterval(interval);
    }

    @Override public List<Context> getContexts() {
        return this.wrappedEntity.getContexts();
    }

    @Override public void setContexts(List<Context> contexts) {
        this.wrappedEntity.setContexts(contexts);
    }

	@Override
	public String getSensorSourceUri() {
		return this.wrappedEntity.getSensorSourceUri();
	}

	@Override
	public void setSensorSourceUri(String sensorSourceUri) {
		this.wrappedEntity.setSensorSourceUri(sensorSourceUri);
	}
}
