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

import de.uniulm.omi.cloudiator.visor.rest.entities.Link;
import de.uniulm.omi.cloudiator.visor.rest.entities.Rel;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by daniel on 27.10.15.
 */
public class ResponseBuilder<T> {

    private T entity;
    private Set<Link> linkSet;

    public ResponseBuilder() {
        this.linkSet = new HashSet<>();
    }

    public static <T> ResponseBuilder<T> newBuilder(Class<T> tClass) {
        return new ResponseBuilder<>();
    }

    public ResponseBuilder<T> entity(T entity) {
        this.entity = entity;
        return this;
    }

    public ResponseBuilder<T> addLink(String href, Rel rel) {
        this.linkSet.add(new Link(href, rel));
        return this;
    }

    public ResponseWrapper<T> build() {
        return new ResponseWrapper<>(entity, linkSet);
    }

}
