# LogUser

> consists of a cookie with the User Name logged in. 
>
> This cookie in the project is can be read by Apache who will log into ʻaccess.log`

## Configuration in the file standalone.xml

```xml
<subsystem xmlns="urn:jboss:domain:undertow:7.0"
           default-server="default-server"
           default-virtual-host="default-host"
           default-servlet-container="default"
           default-security-domain="other">
    <buffer-cache name="default"/>
    <server name="default-server">
        <http-listener name="default" socket-binding="http" redirect-socket="https" enable-http2="true"/>
        <https-listener name="https" socket-binding="https" security-realm="ApplicationRealm" enable-http2="true"/>
        <host name="default-host" alias="localhost">
            <filter-ref name="loguser"/> <!-- # reference the loguser configuration -->
            <single-sign-on/>
            <http-invoker security-realm="ApplicationRealm"/>
        </host>
    </server>
    <servlet-container name="default">
        <jsp-config tag-pooling="false"/>
        <websockets/>
    </servlet-container>
    <filters>
        <!-- # loguser configuration pointing to the module -->
        <filter name="loguser" class-name="com.example.eap.handler.LogUserHandler" module="com.example.eap.handler.loguser"/>
    </filters>
</subsystem>
```



## Module Configuration on JBOSS EAP

Add the module to eap as shown in the following folder structure:

```shell
# generate the project jar
$ mvn clean package
```

> copy the [loguser-1.0.jar](target/loguser-1.0.jar) and the [module.xml](target/classes/module.xml) as shown below for the EAP module folder.

```shell
${JBOSS_HOME}
└── modules
    └── com
        └── example
            └── eap
                └── handler
                    └── loguser
                        └── main
                            ├── loguser-1.0.jar   # file generated in the maven command
                            └── module.xml        # file generated in the maven command
```

# Apache conf

```shell
SetEnvIf Cookie Username=([^;]+) REMOTE_USER=$1
```

# References

 * https://developer.jboss.org/thread/272506

 * http://www.mastertheboss.com/jboss-server/jboss-security/securing-as-7-applications-using-the-applicationrealm

 * https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.2/html/development_guide/undertow

 * https://github.com/svenkubiak/mangooio/issues/262

 * http://www.mastertheboss.com/jboss-web/jbosswebserver/configuring-undertow-filters-on-wildfly

 * Enable samesite for jsessionid cookie - https://stackoverflow.com/questions/49697449/how-to-enable-samesite-for-jsessionid-cookie

 * Configuring Undertow Filters on WildFly - http://www.mastertheboss.com/jboss-web/jbosswebserver/configuring-undertow-filters-on-wildfly

* Configuring Filters - https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.2/html-single/configuration_guide/index#undertow-configure-filters

  > A filter is functionally equivalent to a global valve used in JBoss EAP 6.

* Experiences with migrating from JBoss AS 7 to WildFly 8.1 - http://jdevelopment.nl/experiences-migrating-jboss-7-wildfly-81/

* Provided Undertow Handlers - https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.2/html-single/development_guide/index#reference_material

  1. access-control
  2. **access-log**
  3. allowed-methods
  4. blocking
  5. byte-range
  6. disallowed-methods
  7. compress
  8. trace
  9. ip-access-control
  10. jdbc-access-log
  11. learning-push
  12. resolve-local-name
  13. path-separator
  14. resolve-peer-name
  15. proxy-peer-address
  16. redirect
  17. buffer-request
  18. **dump-request**
  19. request-limit
  20. resource
  21. response-rate-limit
  22. **header <io.undertow.server.handlers.SetHeaderHandler>**
  23. ssl-headers
  24. stuck-thread-detector
  25. url-decoding
