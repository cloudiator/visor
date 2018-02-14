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

package de.uniulm.omi.cloudiator.visor.reporting;

import java.util.Collection;

/**
 * Interface for reporting generic items.
 *
 * @param <T> the class of the reported item.
 * @todo: split in single and multiple...
 */
public interface ReportingInterface<T> {

  /**
   * Reports the generic item.
   *
   * @param item the item to report.
   * @throws ReportingException If an error occurred while reporting the item.
   */
  public void report(T item) throws ReportingException;

  /**
   * Reports a collection of the generic item.
   *
   * @param items a collection of generic items to report.
   * @throws ReportingException If an error occurred while reporting on of the items.
   */
  public void report(Collection<T> items) throws ReportingException;
}
