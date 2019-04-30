package com.logstash.agent;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;
//import java.io.File;
import java.io.FileInputStream;
//import java.io.IOException;
import java.util.Properties;


public class LogstashAgent {

    private static final String AGENT_VERSION = "0.0.1";
    private static final String DEFAULT_CONFIG = "logstash-agent.xml";

    private static Integer collection_interval;
    private static String measurement_prefix;
    private static String influxdb_url;
    private static String logstash_url;

    private static Logger logger = Logger.getLogger(LogstashAgent.class.getName());

    public static void agentmain(String configFile, Instrumentation instrumentation) throws Exception {
        premain(configFile, instrumentation);
    }

    public static void premain(String configFile, Instrumentation instrumentation) throws Exception {
        initializeAgent(configFile);
    }

    public static void main(@NotNull String[] args) {
        String configFile = defaultConfigFile();
        if (args.length > 0) {
            configFile = args[0];
        }
        initializeAgent(configFile);
    }

    private static void printVersion() {
        logger.info("Starting Logstash Agent version: " + AGENT_VERSION);
    }

    private static void initializeAgent(String configFile) {
        printVersion();
        readConfigFile(configFile);
    }

    private static void readConfigFile(String configFile) {
        java.util.Properties properties = new Properties();
        try {
            properties.loadFromXML(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            logger.warning("The configuration file is not found: " + configFile);
        } catch (java.util.InvalidPropertiesFormatException e) {
            logger.warning("Could not parse the configuration file: " + configFile);
        } catch (java.io.IOException e) {
            logger.warning("Could not load configuration file: " + configFile);
        }

        collection_interval = Integer.parseInt(properties.getProperty("collection_interval", "60000"));
        measurement_prefix = properties.getProperty("measurement_prefix", "logstash");
        influxdb_url = properties.getProperty("influxdb_url", "http://127.0.0.1:8186");
        logstash_url = properties.getProperty("logstash_url", "http://127.0.0.1:9600");
    }

    @NotNull
    private static String defaultConfigFile() {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        return rootPath + DEFAULT_CONFIG;
    }
}
