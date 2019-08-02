# wso2-log-configurer
This java application is to overcome the problem of not having a GUI to change log levels and set the additivity values in WSO2 product nodes.

### Pre-requirements
* Running WSO2 product where you can access admin log service.
* Maven
* Java (1.8+)
### Configurations
Default server configurations are stored in ``<HOME>/src/main/resources/serverconfig.json``
You can change the configurations accordingly.

### Build

Use the terminal and go to the home file location

Execute the command ``mvn clean package``

Now copy the `wso2-log-configurer-jar-with-dependencies.jar` file to
a preferred location with a preferred name(wso2-log-configurer.jar). 

### Run

You can execute the following command to see how to use the tool.

`java -jar wso2-logger-configurer.jar --help`

### 

### Available commands

All commands start with `java -jar wso2-logger-configurer.jar`

Default server configuration
```
search <search phrase>
search <search phrase> --starts-with

update <update-log>.json
``` 
#### Passing custom server configuration

Create a json in the following format and save it

```json
{
  "hostname":"https://localhost:9440",
  "username":"admin",
  "password":"admin",
  "systemProperties" : [
    {
      "key":"javax.net.ssl.trustStore",
      "value":"/engagements/mas/wso2-products/wso2ei-6.2.0/repository/resources/security/wso2carbon.jks"
    },
    {
      "key":"javax.net.ssl.trustStorePassword",
      "value": "wso2carbon"
    },
    {
      "key":"javax.net.ssl.trustStoreType",
      "value":"JKS"
    }
  ]
}
```
Now you can use `--config` command to pass the custom configuration.

Example:

``java -jar wso2-logger-configurer.jar --config serverconfig.jar search AUDIT``

#### Update logs JSON file

To update logs, a json file with a single entry, or multiple entries can be used.

Format of a single update json file
```json
{
    "loggerName":"AUDIT_LOG",
    "loggerLevel":"DEBUG",
    "additivity":true
}
```

Format of a bulk update json file
```json
[
    {
    "loggerName":"AUDIT_LOG",
    "loggerLevel":"DEBUG",
    "additivity":true
    },
    {
    "loggerName":"API_LOGGER.HealthCheck",
    "loggerLevel":"DEBUG"
    },
    {
    "loggerName":"API_LOGGER.ECommOrderTriggerAPI",
    "additivity":false
    }
]
```

_Note that the above commands need to be passed in the given order_

Sensitive properties (passwords) can be passed after the above commands

```
--upassword <user password> --kpassword <keystore password>
``` 

Example:

`java -jar wso2-logger-configurer.jar search AUDIT --upassword admin`

_Single or both passwords can be passed in any order after the arguments are passed_

#### Passing server configurations by environment variables

You can define environment variables as follows. They will be prioritized over
the default configuration

| Environment variable              | JSON configuration name           |
| --------------------------------- | --------------------------------- |
| server.hostname                   | hostname                          |
| server.username                   | username                          | 
| server.password                   | password                          |
| javax.net.ssl.trustStore          | javax.net.ssl.trustStore          |
| javax.net.ssl.trustStorePassword  | javax.net.ssl.trustStorePassword  |
| javax.net.ssl.trustStoreType      | javax.net.ssl.trustStoreType      |

### Resources

The default configurations and resources can be found in `<HOME>/resources`

### Samples

Help
![](readmeresources/log-configurer-help.gif)

Search
![](readmeresources/search-defaultconfig.gif)

Update
![](readmeresources/update-default-config.gif)