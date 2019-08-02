package com.wso2.loggingremote.client;

import com.wso2.loggingremote.util.CommonUtil;
import com.wso2.loggingremote.util.LoggerNotFoundException;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.logging.service.LoggingAdminException;
import org.wso2.carbon.logging.service.LoggingAdminStub;

import java.rmi.RemoteException;

public class LoggingAdminServiceClient {

    private final String serviceName = "LoggingAdmin";
    private LoggingAdminStub loggingAdminStub;

    /**
     * @param backEndUrl
     * @param sessionCookie
     * @throws AxisFault
     */
    public LoggingAdminServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
        loggingAdminStub = new LoggingAdminStub(backEndUrl + "/services/" + serviceName);
        ServiceClient serviceClient_;
        Options option_;

        serviceClient_ = loggingAdminStub._getServiceClient();
        option_ = serviceClient_.getOptions();
        option_.setManageSession(true);
        option_.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, sessionCookie);
    }

    public LoggingAdminStub.GetAllLoggerDataResponse searchByLogName(String logName, boolean startWith) {
        LoggingAdminStub.GetAllLoggerDataResponse resp = null;

        try {
            LoggingAdminStub.GetAllLoggerData loggerData = new LoggingAdminStub.GetAllLoggerData();
            loggerData.setLogNameFilter(logName);
            loggerData.setBeginsWith(startWith);

            resp = this.loggingAdminStub.getAllLoggerData(loggerData);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return resp;
    }

    public LoggingAdminStub.GetLoggerDataResponse getCurrentLoggerDetails( String loggerName ) {
        LoggingAdminStub.GetLoggerDataResponse resp = new LoggingAdminStub.GetLoggerDataResponse();
        try{
            LoggingAdminStub.GetLoggerData loggerData = new LoggingAdminStub.GetLoggerData();
            loggerData.setLoggerName(loggerName);
            resp = this.loggingAdminStub.getLoggerData(loggerData);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public boolean updateLogLevel(String logName, String level, Boolean additivity) {
        LoggingAdminStub.UpdateLoggerData loggerData;
        boolean result = false;
        try {
            loggerData = new LoggingAdminStub.UpdateLoggerData();
            loggerData.setLoggerName(logName);
            loggerData.setLoggerLevel(CommonUtil.resolveLog(level));
            loggerData.setAdditivity(additivity);
            loggerData.setPersist(true);
            this.loggingAdminStub.updateLoggerData(loggerData);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoggingAdminException e) {
            e.printStackTrace();
        } catch (LoggerNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        LoginAdminServiceClient authclient;
        try {
            System.setProperty("javax.net.ssl.trustStore","/engagements/mas/wso2-products/wso2ei-6.2.0/repository/resources/security/wso2carbon.jks");
            System.setProperty("javax.net.ssl.trustStorePassword","wso2carbon");
            System.setProperty("javax.net.ssl.trustStoreType","JKS");

            authclient = new LoginAdminServiceClient("https://localhost:9444");
            String session = authclient.authenticate("admin", "admin","https://localhost:9444");

            LoggingAdminServiceClient loggingAdminServiceClient = new LoggingAdminServiceClient("https://localhost:9444", session);
            loggingAdminServiceClient.searchByLogName("", false);
            loggingAdminServiceClient.updateLogLevel("AUDIT_LOG","OFF",false);

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

}
