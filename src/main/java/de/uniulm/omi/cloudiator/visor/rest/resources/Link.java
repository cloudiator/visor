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

/**
 * Created by daniel on 07.04.15.
 */
public class Link {

  private final String href;
  private final Rel rel;

  public Link(String href, Rel rel) {
    this.href = href;
    this.rel = rel;
  }

  public String getHref() {
    return href;
  }

  public String getRel() {
    return rel.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Link link = (Link) o;

    return !(href != null ? !href.equals(link.href) : link.href != null) && rel == link.rel;
  }

  @Override
  public int hashCode() {
    int result = href != null ? href.hashCode() : 0;
    result = 31 * result + (rel != null ? rel.hashCode() : 0);
    return result;
  }
}
