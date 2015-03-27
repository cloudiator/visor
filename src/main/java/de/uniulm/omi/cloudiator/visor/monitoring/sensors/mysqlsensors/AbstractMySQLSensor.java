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

package de.uniulm.omi.cloudiator.visor.monitoring.sensors.mysqlsensors;

import de.uniulm.omi.cloudiator.visor.monitoring.api.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.sensors.AbstractSensor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author zarioha
 *         An abstract probe for measuring the MySQL metadata.
 */
public abstract class AbstractMySQLSensor extends AbstractSensor {
    //TODO have a single connection for all mysql sensor and close it when close monitoring
    static Connection connection;
    //TODO by convention : use '%' user (anonymous) without password to read metadata
    private String jdbcDriver = "org.drizzle.jdbc.DrizzleDriver";
    private String jdbcName = "root";
    private String jdbcPassword = "";
    //TODO configure the URL by another way? (maybe by setMonitorContext())
    private String jdbcUrl = "jdbc:drizzle://localhost:3306/";



    @Override
    // FileConfigurationAccessor
    protected void initialize() throws SensorInitializationException {
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new SensorInitializationException("JdbcDriver not found", e);
        }
        try {
            connection = DriverManager.getConnection(jdbcUrl, jdbcName, jdbcPassword);
        } catch (SQLException e) {
            throw new SensorInitializationException("Error during connection", e);
        }
    }
}
