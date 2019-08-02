package com.wso2.loggingremote.service;

import com.wso2.loggingremote.client.LoggingAdminServiceClient;
import com.wso2.loggingremote.client.LoginAdminServiceClient;
import com.wso2.loggingremote.modal.ServerConfig;
import com.wso2.loggingremote.modal.UpdateLoggerConfig;
import com.wso2.loggingremote.util.CommonUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.logging.service.LoggingAdminStub;

import java.rmi.RemoteException;

public class LoggingService {

    public void listLogs(ServerConfig configurations, String logName, boolean startsWith) {
        LoginAdminServiceClient authclient;
        try {
            //CommonUtil.initialize(configurations.getSystemProperties());
            authclient = new LoginAdminServiceClient(configurations.getHostname());
            String session = authclient.authenticate(configurations.getUsername(),
                    configurations.getPassword(), configurations.getHostname());
            LoggingAdminServiceClient loggingAdminServiceClient = new LoggingAdminServiceClient(configurations.getHostname(), session);
            LoggingAdminStub.GetAllLoggerDataResponse logDataList =
                    loggingAdminServiceClient.searchByLogName(logName, startsWith);

            if (logDataList.get_return() != null && logDataList.get_return().length > 0) {
                System.out.println("Name\t\t\t|Level\t|Parent\t|Additivity");

                for (LoggingAdminStub.LoggerData logdata : logDataList.get_return()) {
                    System.out.println(logdata.getName() + "\t|" + logdata.getLevel() + "\t|" + logdata.getParentName() + "\t|" + logdata.getAdditivity());
                }
            } else {
                System.out.println("No matching loggers found");
            }

            authclient.logOut();
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginAuthenticationExceptionException e) {
            e.printStackTrace();
        } catch (LogoutAuthenticationExceptionException e) {
            e.printStackTrace();
        }
    }

    public void updateLogs(ServerConfig serverConfig, UpdateLoggerConfig loggerConfig) {
        LoginAdminServiceClient authclient;
        try {
            //CommonUtil.initialize(serverConfig.getSystemProperties());

            System.out.println("Logging service started..\n");

            authclient = new LoginAdminServiceClient(serverConfig.getHostname());
            String session = authclient.authenticate(serverConfig.getUsername(),
                    serverConfig.getPassword(), serverConfig.getHostname());

            System.out.println("User authenticated successfully!\n");
            System.out.println("Reading logger changes from file...\n");

            LoggingAdminServiceClient loggingAdminServiceClient =
                    new LoggingAdminServiceClient(serverConfig.getHostname(), session);

            updateLogsAbs(loggerConfig, loggingAdminServiceClient);

            authclient.logOut();

            System.out.println("User logged out successfully");
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginAuthenticationExceptionException e) {
            e.printStackTrace();
        } catch (LogoutAuthenticationExceptionException e) {
            e.printStackTrace();
        }
    }

    public void updateLogs(ServerConfig serverConfig, UpdateLoggerConfig[] loggerBulkConfig) {
        LoginAdminServiceClient authclient;
        try {
            //CommonUtil.initialize(serverConfig.getSystemProperties());

            System.out.println("Logging service started..\n");

            authclient = new LoginAdminServiceClient(serverConfig.getHostname());
            String session = authclient.authenticate(serverConfig.getUsername(),
                    serverConfig.getPassword(), serverConfig.getHostname());

            System.out.println("User authenticated successfully!\n");
            System.out.println("Reading logger changes from file...\n");

            LoggingAdminServiceClient loggingAdminServiceClient =
                    new LoggingAdminServiceClient(serverConfig.getHostname(), session);

            for (UpdateLoggerConfig loggerConfig : loggerBulkConfig) {
                updateLogsAbs(loggerConfig, loggingAdminServiceClient);
            }

            System.out.println("Logs updated successfully!\n");

            authclient.logOut();

            System.out.println("User logged out successfully");
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginAuthenticationExceptionException e) {
            e.printStackTrace();
        } catch (LogoutAuthenticationExceptionException e) {
            e.printStackTrace();
        }
    }

    private void updateLogsAbs(UpdateLoggerConfig loggerConfig, LoggingAdminServiceClient loggingAdminServiceClient) {
        if (StringUtils.isEmpty(loggerConfig.getLoggerName())) {
            System.out.println("Specify logger name");
            return;
        }
        if (StringUtils.isEmpty(loggerConfig.getLoggerLevel()) && loggerConfig.isAdditivity() == null) {
            System.out.println("Specify log level or additivity to update in logger " + loggerConfig.getLoggerName());
            return;
        }
        if (StringUtils.isEmpty(loggerConfig.getLoggerLevel())) {
            System.out.println("Logger level not specified. Fetching current logger level..");
            loggerConfig.setLoggerLevel(loggingAdminServiceClient.getCurrentLoggerDetails(loggerConfig.getLoggerName()).get_return().getLevel());
            System.out.println("Fetching logger level successful!");
        }
        if (loggerConfig.isAdditivity() == null) {
            System.out.println("Additivity not specified. Fetching current additivity..");
            loggerConfig.setAdditivity(loggingAdminServiceClient.getCurrentLoggerDetails(loggerConfig.getLoggerName()).get_return().getAdditivity());
            System.out.println("Fetching additivity successful!");
        }

        loggingAdminServiceClient.updateLogLevel(loggerConfig.getLoggerName(), loggerConfig.getLoggerLevel(), loggerConfig.isAdditivity());

        System.out.println("Log updated successfully for "+loggerConfig.getLoggerName());
    }

}
