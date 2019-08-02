package com.wso2.loggingremote.controller;

import com.wso2.loggingremote.modal.ServerConfig;
import com.wso2.loggingremote.service.LoggingService;
import com.wso2.loggingremote.util.CommonUtil;
import com.wso2.loggingremote.util.Constants;

public class LoggerMain {
    public static void main(String[] args) {
        ServerConfig serverConfig = CommonUtil.loadDefaultServerConfig();
        CommonUtil.initialize(serverConfig.getSystemProperties());
        LoggingService loggingService = new LoggingService();
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(Constants.HELP_TEXT)) {
                CommonUtil.printHelp();
            } else if (args[0].equalsIgnoreCase(Constants.CONFIG_TEXT) && args.length > 2) {
                serverConfig = CommonUtil.loadServerConfig(args[1]);
                //PASSWORDS
                CommonUtil.initialize(serverConfig.getSystemProperties());
                serverConfig = CommonUtil.updatePasswords(serverConfig, args);
                if (CommonUtil.argsLengthBeforePasswords(args) == 5 && args[2].equalsIgnoreCase(Constants.SEARCH_TEXT)) {
                    if (args[4].equalsIgnoreCase(Constants.STARTS_WITH_TEXT)) {
                        loggingService.listLogs(serverConfig, args[3], true);
                    } else {
                        //TODO:SEARCH HELP
                    }
                } else if (CommonUtil.argsLengthBeforePasswords(args) == 4 && args[2].equalsIgnoreCase(Constants.SEARCH_TEXT)) {
                    loggingService.listLogs(serverConfig, args[3], false);
                } else if (CommonUtil.argsLengthBeforePasswords(args) == 4 && args[2].equalsIgnoreCase(Constants.UPDATE_TEXT)) {
                    loggingService.updateLogs(serverConfig, CommonUtil.loadBulkLoggerConfig(args[3]));
                }
            } else if (args[0].equalsIgnoreCase(Constants.SEARCH_TEXT) && args.length > 1) {
                //TODO: PASSWORDS
                serverConfig = CommonUtil.initializeWithEnv(serverConfig);
                serverConfig = CommonUtil.updatePasswords(serverConfig, args);
                if (CommonUtil.argsLengthBeforePasswords(args) == 2) {
                    loggingService.listLogs(serverConfig, args[1], false);
                } else if (CommonUtil.argsLengthBeforePasswords(args) == 3 && args[2].equalsIgnoreCase(Constants.STARTS_WITH_TEXT)) {
                    loggingService.listLogs(serverConfig, args[1], true);
                } else {
                    //TODO:SEARCH HELP
                }
            } else if (args[0].equalsIgnoreCase(Constants.UPDATE_TEXT) && args.length > 1) {
                //TODO: PASSWORDS
                serverConfig = CommonUtil.initializeWithEnv(serverConfig);
                serverConfig = CommonUtil.updatePasswords(serverConfig, args);
                if (CommonUtil.argsLengthBeforePasswords(args) == 2) {
                    loggingService.updateLogs(serverConfig, CommonUtil.loadBulkLoggerConfig(args[1]));
                } else {
                    //TODO:UPDATE HELP
                }
            }

        } else {
            CommonUtil.printCommonErrors();
        }

    }
}
