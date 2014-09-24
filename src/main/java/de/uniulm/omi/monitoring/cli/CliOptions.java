package de.uniulm.omi.monitoring.cli;

import de.uniulm.omi.monitoring.MonitoringAgent;
import org.apache.commons.cli.*;

/**
 * Created by daniel on 24.09.14.
 */
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
}
