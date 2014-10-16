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

package de.uniulm.omi.monitoring.cli;

import de.uniulm.omi.monitoring.MonitoringAgent;
import org.apache.commons.cli.*;

/**
 * Created by daniel on 24.09.14.
 */
@SuppressWarnings("AccessStaticViaInstance")
public class CliOptions {

    private static Options options;
    private static CommandLine commandLine = null;
    private static BasicParser parser = new BasicParser();
    private static HelpFormatter helpFormatter = new HelpFormatter();

    static {
        //build the options
        options = new Options();
        options.addOption(OptionBuilder
                        .withLongOpt("kairosUrl")
                        .withDescription("Url of the kairosDB")
                        .isRequired()
                        .hasArg()
                        .create("kip")
        );
        options.addOption(OptionBuilder
                        .withLongOpt("kairosPort")
                        .withDescription("Port of the kairosDB")
                        .isRequired()
                        .hasArg()
                        .create("kp")
        );
        options.addOption(OptionBuilder
                        .withLongOpt("localIp")
                        .withDescription("IP of the local machine")
                        .isRequired()
                        .hasArg()
                        .create("ip")
        );
        options.addOption(OptionBuilder
                        .withLongOpt("port")
                        .withDescription("Port on which the telnet server should start")
                        .hasArg()
                        .create("p")
        );
    }

    public static void setArguments(String[] args) throws ParseException {
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp();
            System.exit(1);
        }
    }

    public static void printHelp() {
        helpFormatter.printHelp(MonitoringAgent.class.getCanonicalName(), options);
    }

    protected static String getCommandLineOption(String name) {
        if (commandLine == null) {
            throw new IllegalStateException("Command Line Arguments not yet parsed");
        }
        if (!commandLine.hasOption(name)) {
            return null;
        }
        return commandLine.getOptionValue(name);
    }

    public static String getLocalIp() {
        return getCommandLineOption("ip");
    }

    public static String getKairosServer() {
        return getCommandLineOption("kip");
    }

    public static String getKairosPort() {
        return getCommandLineOption("kp");
    }

    public static Integer getPort() {
        if(getCommandLineOption("p") == null) {
            return null;
        }
        return Integer.valueOf(getCommandLineOption("p"));
    }
}
