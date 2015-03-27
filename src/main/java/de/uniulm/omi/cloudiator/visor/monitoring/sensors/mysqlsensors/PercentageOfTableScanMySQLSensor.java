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

import de.uniulm.omi.cloudiator.visor.monitoring.api.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.api.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.monitoring.api.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MeasurementImpl;
import de.uniulm.omi.cloudiator.visor.monitoring.impl.MonitorContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zarioha
 *         A probe for measuring the MySQL metadata : Percentage of full table scans
 */
public class PercentageOfTableScanMySQLSensor extends AbstractMySQLSensor {
    private PreparedStatement ps;

    @Override protected void initialize() throws SensorInitializationException {
        super.initialize();

        try {
            // "/*!50002 GLOBAL */" return 0 values WHEN it is on a prepared statment (not in executeQuery method)
            this.ps = connection.prepareStatement("SHOW GLOBAL STATUS where Variable_name like ?");
            this.ps.setString(1, "Handler_read%");
        } catch (SQLException e) {
            throw new SensorInitializationException("Error prepared query", e);
        }
    }

    @Override protected Measurement getMeasurement(MonitorContext monitorContext)
        throws MeasurementNotAvailableException {

        try {
            Map<String, Integer> handlers = new HashMap<String, Integer>();
            ResultSet rs = this.ps.executeQuery();
            long queryTimeMillis = System.currentTimeMillis();

            while (rs.next()) {
                handlers.put(rs.getString("Variable_name"), rs.getInt("Value"));
            }

            int handlerReadKey = handlers.get("Handler_read_key");
            int handlerReadPrev = handlers.get("Handler_read_prev");
            int handlerReadRndNext = handlers.get("Handler_read_rnd_next");
            int handlerReadNext = handlers.get("Handler_read_next");
            int handlerReadRnd = handlers.get("Handler_read_rnd");
            int handlerReadFirst = handlers.get("Handler_read_first");

            float percentageFullTableScans =
                ((float) (handlerReadRndNext + handlerReadRnd) / (handlerReadRndNext
                    + handlerReadRnd + handlerReadFirst + handlerReadNext + handlerReadKey
                    + handlerReadPrev));

            return new MeasurementImpl(queryTimeMillis, percentageFullTableScans * 100);
        } catch (SQLException e) {
            throw new MeasurementNotAvailableException("Error query execution", e);
        }
    }
}
