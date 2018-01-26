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

package de.uniulm.omi.cloudiator.visor.config;

import com.google.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daniel on 10.02.15.
 */
@Singleton
public class AwsIpWebService implements IpProvider {

  private static final String AWS_SERVICE = "http://checkip.amazonaws.com";
  private static final Logger LOGGER = LoggerFactory.getLogger(AwsIpWebService.class);
  private String ipCache;

  private static String contactService() {
    URL whatIsMyIp;
    BufferedReader in = null;
    try {
      LOGGER.debug("Contacting AWS IP service at " + AWS_SERVICE);
      whatIsMyIp = new URL(AWS_SERVICE);
      in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream(), "UTF-8"));
      String ip = in.readLine();
      LOGGER.info("AWS IP service returned " + ip + " as public ip");
      return ip;
    } catch (IOException e) {
      LOGGER.error("Error contacting AWS IP service.", e);
      return null;
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ignored) {
          LOGGER.warn("Error contacting AWS service", ignored);
        }
      }
    }
  }

  @Nullable
  @Override
  public String getPublicIp() {
    if (this.ipCache == null) {
      this.ipCache = contactService();
    }
    return this.ipCache;
  }
}
