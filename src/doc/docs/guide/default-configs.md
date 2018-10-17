# Default config files

## Yaml config

Dropwizard configuration with both orient and security configs declared as yaml: 

```yaml
orient-server:
  files-path: $TMP/db/

  config:
    users:
      - name: root
        password: root
        resources: '*'
      - name: guest
        password: guest
        resources: 'connect,server.listDatabases,server.dblist'

    handlers:
    # enable below lines if graph version used and gremlin support required
#      - clazz: com.orientechnologies.orient.graph.handler.OGraphServerHandler
#        parameters:
#            - enabled: true
#            - graph.pool.max: 50
    # enable for distributed mode support (requires extra orientdb-distributed dependency)
#      - clazz: com.orientechnologies.orient.server.hazelcast.OHazelcastPlugin
#        parameters:
#            - enabled: true
#            - configuration.db.default: '${ORIENTDB_HOME}/config/default-distributed-db-config.json'
#            - configuration.hazelcast: '${ORIENTDB_HOME}/config/hazelcast.xml'
      - clazz: com.orientechnologies.orient.server.handler.OJMXPlugin
        parameters:
            - enabled: true
            - profilerManaged: true
      - clazz: com.orientechnologies.orient.server.handler.OAutomaticBackup
        parameters:
        # may be configured with separate json file (see distribution)
#            - config: '${ORIENTDB_HOME}/config/automatic-backup.json'
            - enabled: false
            - mode: FULL_BACKUP
            - exportOptions:
            - firstTime: 23:00:00
            - delay: 4h
            - target.directory: backup
            - target.fileName: '${DBNAME}-${DATE:yyyyMMddHHmmss}.zip'
            - compressionLevel: 9
            - bufferSize: 1048576
            - db.include:
            - db.exclude:
      - clazz: com.orientechnologies.orient.server.handler.OServerSideScriptInterpreter
        parameters:
            - enabled: true
            - allowedLanguages: SQL

    network:
    # example of custom sockets configuration
#      sockets:
#        - name: ssl
#          implementation: com.orientechnologies.orient.server.network.OServerTLSSocketFactory
#          parameters:
#            - network.ssl.clientAuth: false
#            - network.ssl.keyStore: 'config/cert/orientdb.ks'
#            - network.ssl.keyStorePassword: password
#            - network.ssl.trustStore: 'config/cert/orientdb.ks'
#            - network.ssl.trustStorePassword: password
#        - name: https
#          implementation: com.orientechnologies.orient.server.network.OServerTLSSocketFactory
#          parameters:
#            - network.ssl.clientAuth: false
#            - network.ssl.keyStore: 'config/cert/orientdb.ks'
#            - network.ssl.keyStorePassword: password
#            - network.ssl.trustStore: 'config/cert/orientdb.ks'
#            - network.ssl.trustStorePassword: password
      protocols:
        - binary: com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary
        - http: com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb
      listeners:
        - protocol: binary
          ipAddress: 0.0.0.0
          portRange: 2424-2430
          socket: default
        - protocol: http
          ipAddress: 0.0.0.0
          portRange: 2480-2490
          socket: default
          parameters:
              - network.http.charset: utf-8
              - network.http.jsonResponseError: true
          commands:
              - pattern: 'GET|www GET|studio/ GET| GET|*.htm GET|*.html GET|*.xml GET|*.jpeg GET|*.jpg GET|*.png GET|*.gif GET|*.js GET|*.css GET|*.swf GET|*.ico GET|*.txt GET|*.otf GET|*.pjs GET|*.svg GET|*.json GET|*.woff GET|*.woff2 GET|*.ttf GET|*.svgz'
                implementation: com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent
                stateful: false
                parameters:
                    - http.cache:*.htm *.html: 'Cache-Control: no-cache, no-store, max-age=0, must-revalidate\r\nPragma: no-cache'
                    - http.cache:default: 'Cache-Control: max-age=120'
              # enable below lines if graph version used and gephi support required
#              - pattern: 'GET|gephi/*'
#                stateful: false
#                implementation: com.orientechnologies.orient.graph.server.command.OServerCommandGetGephi

    properties:
       - plugin.dynamic: true
       - plugin.hotReload: false
       - db.pool.min: 1
       - db.pool.max: 50
       - profiler.enabled: true
         #configures the profiler as <seconds-for-snapshot>,<archive-snapshot-size>,<summary-size>
       - profiler.config: '30,10,10'

  security:
    enabled: true
    debug: false
    server:
      createDefaultUsers: true
    authentication:
      enabled: true
      allowDefault: true
      authenticators:
         - name: Password
           class: com.orientechnologies.orient.server.security.authenticator.ODefaultPasswordAuthenticator
           enabled: true
           users:
             - username: "guest"
               resources: "server.listDatabases,server.dblist"
         - name: ServerConfig
           class: com.orientechnologies.orient.server.security.authenticator.OServerConfigAuthenticator
           enabled: true
         - name: SystemAuthenticator
           class: com.orientechnologies.orient.server.security.authenticator.OSystemUserAuthenticator
           enabled: true
    auditing:
      class: com.orientechnologies.security.auditing.ODefaultAuditing
      enabled: false
``` 

## Xml config

The same as previous, but orient configuration declared in external xml file:

```yaml
orient-server:
  files-path: $TMP/db/
  config-file: 'conf/sample.xml'
  
  # security section omitted
```

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<orient-server>
    <handlers>
        <!-- enable below lines if graph version used and gremlin support required -->
        <!--<handler class="com.orientechnologies.orient.graph.handler.OGraphServerHandler">-->
            <!--<parameters>-->
                <!--<parameter value="true" name="enabled"/>-->
                <!--<parameter value="50" name="graph.pool.max"/>-->
            <!--</parameters>-->
        <!--</handler>-->
        <!-- enable for distributed usage (requires orientdb-distributed dependency) -->
        <!--<handler class="com.orientechnologies.orient.server.hazelcast.OHazelcastPlugin">-->
            <!--<parameters>-->
                <!--<parameter value="${distributed}" name="enabled"/>-->
                <!--<parameter value="${ORIENTDB_HOME}/config/default-distributed-db-config.json" name="configuration.db.default"/>-->
                <!--<parameter value="${ORIENTDB_HOME}/config/hazelcast.xml" name="configuration.hazelcast"/>-->
            <!--</parameters>-->
        <!--</handler>-->
        <!-- JMX SERVER, TO TURN ON SET THE 'ENABLED' PARAMETER TO 'true' -->
        <handler class="com.orientechnologies.orient.server.handler.OJMXPlugin">
            <parameters>
                <parameter value="false" name="enabled"/>
                <parameter value="true" name="profilerManaged"/>
            </parameters>
        </handler>
        <!-- AUTOMATIC BACKUP, TO TURN ON SET THE 'ENABLED' PARAMETER TO 'true' -->
        <handler class="com.orientechnologies.orient.server.handler.OAutomaticBackup">
            <parameters>
                <parameter value="false" name="enabled"/>
                <!-- separate config file may be used with (see original orient distribution config) -->
                <!--<parameter value="${ORIENTDB_HOME}/config/automatic-backup.json" name="config"/>-->
                <parameter name="enabled" value="false"/>
                <parameter name="mode" value="FULL_BACKUP"/>
                <parameter name="exportOptions" value=""/>
                <parameter name="delay" value="4h"/>
                <parameter name="firstTime" value="23:00:00"/>
                <parameter name="target.directory" value="backup"/>
                <parameter name="target.fileName" value="${DBNAME}-${DATE:yyyyMMddHHmmss}.zip"/>
                <parameter name="compressionLevel" value="9"/>
                <parameter name="bufferSize" value="1048576"/>
                <!-- if empty, backups all databases -->
                <parameter name="db.include" value=""/>
                <parameter name="db.exclude" value=""/>
                <!-- USE COMMA TO SEPARATE MULTIPLE DATABASE NAMES -->
            </parameters>
        </handler>
        <!-- SERVER SIDE SCRIPT INTERPRETER. WARNING! THIS CAN BE A SECURITY HOLE:
            ENABLE IT ONLY IF CLIENTS ARE TRUST, TO TURN ON SET THE 'ENABLED' PARAMETER
            TO 'true' -->
        <handler class="com.orientechnologies.orient.server.handler.OServerSideScriptInterpreter">
            <parameters>
                <parameter value="true" name="enabled"/>
                <parameter value="SQL" name="allowedLanguages"/>
            </parameters>
        </handler>
    </handlers>
    <network>
        <sockets>
            <socket implementation="com.orientechnologies.orient.server.network.OServerTLSSocketFactory" name="ssl">
                <parameters>
                    <parameter value="false" name="network.ssl.clientAuth"/>
                    <parameter value="config/cert/orientdb.ks" name="network.ssl.keyStore"/>
                    <parameter value="password" name="network.ssl.keyStorePassword"/>
                    <parameter value="config/cert/orientdb.ks" name="network.ssl.trustStore"/>
                    <parameter value="password" name="network.ssl.trustStorePassword"/>
                </parameters>
            </socket>
            <socket implementation="com.orientechnologies.orient.server.network.OServerTLSSocketFactory" name="https">
                <parameters>
                    <parameter value="false" name="network.ssl.clientAuth"/>
                    <parameter value="config/cert/orientdb.ks" name="network.ssl.keyStore"/>
                    <parameter value="password" name="network.ssl.keyStorePassword"/>
                    <parameter value="config/cert/orientdb.ks" name="network.ssl.trustStore"/>
                    <parameter value="password" name="network.ssl.trustStorePassword"/>
                </parameters>
            </socket>
        </sockets>
        <protocols>
            <protocol implementation="com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary" name="binary"/>
            <protocol implementation="com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb" name="http"/>
        </protocols>
        <listeners>
            <listener protocol="binary" socket="default" port-range="2424-2430" ip-address="0.0.0.0"/>
            <listener protocol="http" socket="default" port-range="2480-2490" ip-address="0.0.0.0">
                <commands>
                    <command implementation="com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent" pattern="GET|www GET|studio/ GET| GET|*.htm GET|*.html GET|*.xml GET|*.jpeg GET|*.jpg GET|*.png GET|*.gif GET|*.js GET|*.css GET|*.swf GET|*.ico GET|*.txt GET|*.otf GET|*.pjs GET|*.svg GET|*.json GET|*.woff GET|*.woff2 GET|*.ttf GET|*.svgz" stateful="false">
                        <parameters>
                            <entry value="Cache-Control: no-cache, no-store, max-age=0, must-revalidate\r\nPragma: no-cache" name="http.cache:*.htm *.html"/>
                            <entry value="Cache-Control: max-age=120" name="http.cache:default"/>
                        </parameters>
                    </command>
                    <command implementation="com.orientechnologies.orient.graph.server.command.OServerCommandGetGephi" pattern="GET|gephi/*" stateful="false"/>
                </commands>
                <parameters>
                    <parameter value="utf-8" name="network.http.charset"/>
                    <parameter value="true" name="network.http.jsonResponseError"/>
                </parameters>
            </listener>
        </listeners>
    </network>
    <storages/>
    <users>
        <user resources="*" password="root" name="root"/>
        <user resources="connect,server.listDatabases,server.dblist" password="guest" name="guest"/>
    </users>
    <properties>
        <entry value="1" name="db.pool.min"/>
        <entry value="50" name="db.pool.max"/>
        <entry value="false" name="profiler.enabled"/>
    </properties>
</orient-server>
```

### Json security config

Security config defined in external json file:

```yaml
orient-server:
  files-path: $TMP/db/
  # may be declared as yaml
  config-file: 'conf/sample.xml'
   
  security-file: 'conf/security.json'
```

```json
{
  "enabled": true,
  "debug": false,
  "server": {
    "createDefaultUsers": true
  },
  "authentication": {
    "enabled": true,
    "allowDefault": true,
    "authenticators": [
      {
        "name": "Password",
        "class": "com.orientechnologies.orient.server.security.authenticator.ODefaultPasswordAuthenticator",
        "enabled": true,
        "users": [
          {
            "username": "guest",
            "resources": "server.listDatabases,server.dblist"
          }
        ]
      },
      {
        "name": "ServerConfig",
        "class": "com.orientechnologies.orient.server.security.authenticator.OServerConfigAuthenticator",
        "enabled": true
      },
      {
        "name": "SystemAuthenticator",
        "class": "com.orientechnologies.orient.server.security.authenticator.OSystemUserAuthenticator",
        "enabled": true
      } ]
  },
  "auditing": {
    "class": "com.orientechnologies.security.auditing.ODefaultAuditing",
    "enabled": false
  }
}
```