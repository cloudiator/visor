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

package de.uniulm.omi.cloudiator.visor.telnet;

import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;

/**
 * Parses an object of type T into an object of type S.
 *
 * @param <T> Type of the input.
 * @param <S> Type of the output.
 */
public interface RequestParsingInterface<T, S> {

    /**
     * Executes the parsing action.
     *
     * @param t the object to parse.
     * @return the resulting object
     * @throws ParsingException if the object is not parsable.
     */
    S parse(T t) throws ParsingException;

    /**
     * @return the context of the request parser.
     */
    MonitorContext monitorContext();


}
