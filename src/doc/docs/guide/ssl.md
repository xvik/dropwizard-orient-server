# SSL

## Orient configuration

[Orient ssl guide](https://orientdb.com/docs/2.2/Using-SSL-with-OrientDB.html) describes that: 

* ssl could be configured for binary protocol and https for http.
* ssl is configured using custom socket (may be the same for both binary and http listeners)
* both secured and not secured listeners could co-exist 

Example ssl only configuration:

```yaml
    network:
      sockets:
        - name: ssl
          implementation: com.orientechnologies.orient.server.network.OServerTLSSocketFactory
          parameters:
            - network.ssl.clientAuth: false
            - network.ssl.keyStore: 'path/to/example.keystore'
            - network.ssl.keyStorePassword: example
            - network.ssl.trustStore: 'path/to/example.keystore'
            - network.ssl.trustStorePassword: example
      protocols:
        - binary: com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary
        - http: com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb
      listeners:
        - protocol: binary
          ipAddress: 0.0.0.0
          portRange: 2434-2440
          socket: ssl
        - protocol: http
          ipAddress: 0.0.0.0
          portRange: 2480-2490
          socket: ssl
          ...
```

Note that socket parameter of listeners set to `ssl` instead of `default`. 

Mixed configuration:

```yaml
    network:
      sockets:
        - name: ssl
          implementation: com.orientechnologies.orient.server.network.OServerTLSSocketFactory
          parameters:
            - network.ssl.keyStore: 'path/to/example.keystore'
            - network.ssl.keyStorePassword: example

      protocols:
        - binary: com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary
        - http: com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb
      listeners:
        - protocol: binary
          ipAddress: 0.0.0.0
          portRange: 2424-2430
          socket: default
        - protocol: binary
          ipAddress: 0.0.0.0
          portRange: 2434-2440
          socket: ssl
        - protocol: http
          ipAddress: 0.0.0.0
          portRange: 2480-2490
          socket: http
          ...
```

Two listeners registered for binary protocol: not secured (2424) and secured (2434). Two versions of http listeners could also be registered.

Note that default port ranges for binary ssl are 2434-2440. This is important as orient tries to connect to 2434 by default, when ssl is enabled on client (when port is not declared in address: `remote:localhost/somedb`). Different ports could be used, but it will force remote clients to always specify port (`remote:localhost:2455/somedb`).

## Certificate paths

By default, orient will try to locate socket certificates relative to `files-path` (ORIENTDB_HOME) directory, which might be not handful. To allow defining certificates relative to application startup dir, bundle will check registered ssl bundles and if configured keystorage files exist relatively to app dir, then it will change relative paths to absolute file paths.

For example, 

```yaml
      sockets:
        - name: ssl
          implementation: com.orientechnologies.orient.server.network.OServerTLSSocketFactory
          parameters:
            - network.ssl.clientAuth: false
            - network.ssl.keyStore: 'conf/certs/example.keystore'
            ...
```

If file `/app/run/dir/conf/certs/example.keystore` exists then this absolute path will be set into orient config (instead of relative). If file not found, then orient will try to resolve file against `files-path` directory.

## /orient info servlet

Orient info servlet shows all configured ports and highlights secured ports:

```
* Binary ports: 2424, 2434 (ssl)
* Http ports: 2480, 2491 (ssl)
```

## Studio url

If multiple http listeners defined, bundle will install studio only in one of them:

* Prefer https listener
* If no secure listener found, use the first http listener

/orient/studio alias will lead to https studio version (assuming https listener registered).

Dropwizard admin context ssl configuration is not counted (can't be), so alias will always properly redirect to the studio. For example, if admin context use https and orient is using http, then `https://localhost:8444/orient/studio will redirect to http url like `http://localhost:2480/studio` (opposite case is also possible).

## Auto SSL configuration

The special EXPERIMENTAL option provided to automate orient ssl configuration based on dropwizard main context configuration. 

For example, dropwizard main connector is [configured to use https](https://www.dropwizard.io/en/release-2.0.x/manual/core.html#ssl):

```yaml
server:
  applicationConnectors:
      - type: https
        port: 8443
        keyStorePath: path/to/example.keystore
        keyStorePassword: example
        validateCerts: false
        validatePeers: false
```

And `auto-ssl` option is enabled (and no ssl manually configured for orient):

```yaml
orient-server:
  files-path: $TMP/db/
  auto-ssl: true
  config:
     ...
     network:
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
```

Then on startup new ssl socket will be added to the configuration (with the same keystore and truststore as in dropwizard) and both listeneres will be switched to use it. So you would see in /orient page:

```
* Binary ports: 2434 (ssl)
* Http ports: 2480 (ssl)
```

!!! important 
    It changed(!) default 2424-2430 port range for binary protocol to 2434-2440 because it is orient defaults for ssl (and remote connection from client will assume 2434 port by default). BUT, if port range will be different (even if it would be different only by 1: 2424-2429) then ports will not(!) be switched.

Also, auto-ssl mode automatically configures [orient client to use ssl](https://orientdb.com/docs/2.2/Using-SSL-with-OrientDB.html#client-configuration): `OGlobalConfiguration.CLIENT_USE_SSL.setValue(true);`. It is done ONLY when auto-ssl enabled because only in this case bundle could be sure that client must use secured connection (and for sure client will call embedded server).

This allows you to always use remote connection in the simplest way: `remote:localhost/somedb` (because port is corrected to ssl default automatically and client ssl mode also enabled). Convinient for tests.

Auto ssl option may be useful for dev environments to check something quickly. The option was not intended to be used for production due to its limitations:

* Will not configure orient ssl: 
  - if at least one listener already use ssl
  - if dropwizard https connector specifies keystore providers (orient does not support providers configuration)
* All configured listeners are changed to use ssl, so it is impossible to specify additional not secured listener when auto ssl enabled
* Default ports change for bianray protocol and orient client ssl enabling could be confusing