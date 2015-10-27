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

package de.uniulm.omi.cloudiator.visor.rest.controllers;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.uniulm.omi.cloudiator.visor.rest.entities.Link;

import java.util.Collection;

/**
 * Created by daniel on 27.10.15.
 */
public class ResponseWrapper<T> {

    @JsonUnwrapped private final T entity;
    private final Collection<Link> links;

    public ResponseWrapper(T entity, Collection<Link> links) {
        this.entity = entity;
        this.links = links;
    }

    public T getEntity() {
        return entity;
    }

    public Collection<Link> getLinks() {
        return links;
    }
}
