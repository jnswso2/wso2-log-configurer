package com.wso2.loggingremote.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.loggingremote.modal.Configurations;
import com.wso2.loggingremote.modal.ServerConfig;
import com.wso2.loggingremote.modal.SystemProperty;
import com.wso2.loggingremote.modal.UpdateLoggerConfig;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.List;

public class CommonUtil {

    /**
     * Method to resolve proper name which would be passed to Admin Logger Service
     *
     * @param log_
     * @return
     * @throws LoggerNotFoundException
     */
    public static String resolveLog(String log_) throws LoggerNotFoundException {
        if (log_.trim().matches("(?i)OFF|TRACE|DEBUG|INFO|WARN|ERROR|FATAL")) {
            return log_.trim().toUpperCase();
        } else {
            throw new LoggerNotFoundException();
        }
    }

    public static void initialize(List<SystemProperty> propertyList) {
        //clear previous
        System.clearProperty(Constants.TRUST_STORE_PHR);
        System.clearProperty(Constants.TRUST_STORE_PASSWORD_PHR);
        System.clearProperty(Constants.TRUST_STORE_TYPE_PHR);
        //add new properties
        for (SystemProperty property : propertyList) {
            if (property.getKey() != null && property.getValue() != null)
                System.setProperty(property.getKey(), property.getValue());
        }

    }

    public static ServerConfig initializeWithEnv(ServerConfig config) {
        //setting system properties
        if (System.getenv(Constants.TRUST_STORE_PHR) != null) {
            System.setProperty(Constants.TRUST_STORE_PHR, System.getenv(Constants.TRUST_STORE_PHR));
        }
        if (System.getenv(Constants.TRUST_STORE_TYPE_PHR) != null) {
            System.setProperty(Constants.TRUST_STORE_TYPE_PHR, System.getenv(Constants.TRUST_STORE_TYPE_PHR));
        }
        if (System.getenv(Constants.TRUST_STORE_PASSWORD_PHR) != null) {
            System.setProperty(Constants.TRUST_STORE_PASSWORD_PHR, System.getenv(Constants.TRUST_STORE_PASSWORD_PHR));
        }
        if (System.getenv(Constants.SERVER_HOSTNAME) != null) {
            config.setHostname(System.getenv(Constants.SERVER_HOSTNAME));
        }
        if (System.getenv(Constants.SERVER_USERNAME) != null) {
            config.setHostname(System.getenv(Constants.SERVER_USERNAME));
        }
        if (System.getenv(Constants.SERVER_PASSWORD) != null) {
            config.setHostname(System.getenv(Constants.SERVER_PASSWORD));
        }

        return config;
    }

    public static ServerConfig loadServerConfig(String serverConfigPath) {
        ObjectMapper mapper = new ObjectMapper();
        ServerConfig serverConfig = new ServerConfig();
        try {
            serverConfig = mapper.readValue(new File(serverConfigPath), ServerConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverConfig;
    }

    public static ServerConfig loadDefaultServerConfig() {
        InputStream inputStream = CommonUtil.class
                .getClassLoader().getResourceAsStream(Constants.DEFAULT_SERVER_CONFIG_FILE);
        BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            line = buf.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileAsString = sb.toString();
        ObjectMapper mapper = new ObjectMapper();
        ServerConfig serverConfig = new ServerConfig();
        try {
            serverConfig = mapper.readValue(fileAsString, ServerConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverConfig;
    }

    public static UpdateLoggerConfig loadLoggerConfig(String loggerConfigPath) {
        ObjectMapper mapper = new ObjectMapper();
        UpdateLoggerConfig loggerConfig = new UpdateLoggerConfig();
        try {
            loggerConfig = mapper.readValue(new File(loggerConfigPath), UpdateLoggerConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loggerConfig;
    }

    public static UpdateLoggerConfig[] loadBulkLoggerConfig(String loggerConfigPath) {
        ObjectMapper mapper = new ObjectMapper();
        UpdateLoggerConfig[] loggerConfig = null;
        try {
            loggerConfig = mapper.readValue(readAndProcessFileContent(loggerConfigPath), UpdateLoggerConfig[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loggerConfig;
    }

    private static String readAndProcessFileContent(String loggerConfig) throws IOException {
        InputStream is = new FileInputStream(loggerConfig);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        String fileAsString = sb.toString().replace("\n", "").replace("\t", "");
        if (!fileAsString.startsWith("[")) {
            fileAsString = "[" + fileAsString + "]";
        }
        return fileAsString;
    }

    public static Configurations loadConfigurations(String serverConfigPath, String loggerConfigPath) {
        Configurations configurations = new Configurations();
        ObjectMapper mapper = new ObjectMapper();

        ServerConfig serverConfig;
        try {
            serverConfig = mapper.readValue(new File(serverConfigPath), ServerConfig.class);
            configurations.setServerConfig(serverConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (StringUtils.isNotEmpty(loggerConfigPath)) {
            UpdateLoggerConfig loggerConfig;
            try {
                loggerConfig = mapper.readValue(new File(loggerConfigPath), UpdateLoggerConfig.class);
                configurations.setLoggerConfig(loggerConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return configurations;
    }

    public static ServerConfig updatePasswords(ServerConfig config, String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase(Constants.KEYSTORE_PASSWORD)) {
                if (args.length > i) {
                    System.setProperty(Constants.TRUST_STORE_PASSWORD_PHR, args[i + 1]);
                } else {
                    System.out.println("Provide keystore password after " + Constants.KEYSTORE_PASSWORD);
                }
            }
            if (args[i].equalsIgnoreCase(Constants.USER_PASSWORD)) {
                if (args.length > i) {
                    config.setPassword(args[i + 1]);
                } else {
                    System.out.println("Provide user password after " + Constants.USER_PASSWORD);
                }
            }
        }

        return config;
    }

    public static int argsLengthBeforePasswords(String[] args) {
        int length = args.length;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase(Constants.KEYSTORE_PASSWORD) || args[i].equalsIgnoreCase(Constants.USER_PASSWORD)) {
                length = i;
                break;
            }
        }
        //CommonUtil.argsLengthBeforePasswords(args)
        return length;
    }

    public static void printHelp() {
        System.out.println("Welcome to wso2 remote log enable service..");
        System.out.println("\nTo search for logs use the following command");
        System.out.println("\tjava -jar logging-remote.jar --search {logger phrase (string)} {starts with (boolean)}");
        //TODO: create the full help log
    }

    public static void printUpdateErrors() {
        System.out.println("Please provide path for server configurations and logger configurations");
        System.out.println("Proper format");
        System.out.println("\t--update <serverConfig> <loggerConfig>");
        System.out.println("For more details run the application with --help");
    }

    public static void printCommonErrors() {
        System.out.println("Please provide a valid command");
        System.out.println("For more details run the application with --help");
    }

    public static void printSearchErrors() {
        System.out.println("Please provide path for server configurations and logger configurations");
        System.out.println("Proper format");
        System.out.println("\t--update <serverConfig> <logger_phrase> <starts_with>");
        System.out.println("For more details run the application with --help");
    }
}
