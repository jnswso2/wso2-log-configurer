package com.wso2.loggingremote.util;

public class Constants {
    //server config constants
    public static final String TRUST_STORE_PHR = "javax.net.ssl.trustStore";
    public static final String TRUST_STORE_PASSWORD_PHR = "javax.net.ssl.trustStorePassword";
    public static final String TRUST_STORE_TYPE_PHR = "javax.net.ssl.trustStoreType";
    public static final String SERVER_HOSTNAME = "server.hostname";
    public static final String SERVER_USERNAME = "server.username";
    public static final String SERVER_PASSWORD = "server.password";

    public static final String DEFAULT_SERVER_CONFIG_FILE = "serverconfig.json";
    //arguments
    public static final String HELP_TEXT = "--help";
    public static final String CONFIG_TEXT = "--config";
    public static final String STARTS_WITH_TEXT = "--starts-with";
    public static final String SEARCH_TEXT = "search";
    public static final String UPDATE_TEXT = "update";
    public static final String KEYSTORE_PASSWORD = "--kpassword";
    public static final String USER_PASSWORD = "--upassword";
}
