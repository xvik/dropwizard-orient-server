# Configuration

Define orient section in application config yaml file:

```yaml
orient-server:
  start: true
  files-path: $TMP/db/
  
  config:
    ...
  
  # optional orient security configuration  
  security:
    ...
```

!!! note
    Orient server configuration could be declared directly in dropwizard configuration yaml or 
    as external xml file (like in orient distribution).

| Option    | Description |
|-----------|-------------|
| `start`  | enables or disables orient server start (the same effect will be if orient configuration section will not exist, this option exist to allow disabling server without removing entire config section) | 
| `admin-servlet` | enables or disables orient admin servlet installation (`/orient`). Enabled by default|
| `files-path` | defines folder, where orient will store database files. May be not existing directory - orient will create it when necessary. Will be set as value for `ORIENTDB_HOME` environment variable |
| `config` |section defines [orient server configuration](http://orientdb.com/docs/3.0.x/internals/DB-Server.html). Orient use xml format for configuration files and this section is simply yaml representation of xml config. Special shortcuts supported for properties and parameters sections (see [example configuration](default-configs.md#yaml-config)).|
| `config-file` | used to specify path to xml configuration file instead of direct yaml configuration in 'config' section. See [example xml config](default-configs.md#xml-config) (taken from orient distribution)|
| `security`| section defines [orient security configuration](http://orientdb.com/docs/3.0.x/security/Security-Config.html). In orient distribution security configured with security.json file. This section is implicitly converted to json file and configured for orient. Optional: when no server configuration defined, orient only prints error log message, but everything works.|
| `security-file` | used to specify path to security.json file instead of direct yaml configuration in 'security' section. See [example json config](default-configs.md#json-security-config)|
| `auto-ssl` | used to [enable automatic ssl configuration for orient listeners](ssl.md#auto-ssl-configuration), when dropwizard main context is configured to use https. This is experimental feature. |

Path properties `files-path`, `config-file` and `security-file` may use special symbols:

| Property | Description |
| ---- | ----- |
| $TMP | system temp directory (java.io.tmpdir) |
| $APP_HOME | application starting directory ('.') |
| $FILES_HOME | directory configured by `files-path` property|
| ${prop} | where prop is any system property or environment variable|

!!! IMPORTANT
    User `root` must be defined in configuration, because orient 2 asks for root user password on start, and in embedded mode it can't save it (so will ask on each start).
    To avoid this case, error is thrown if no 'root' user defined.

Also, note that server users and database users are different! In default configuration `root` and `guest` users defined for server.
When new database created, orient will create default database users: `admin`, `reader`, `writer`. 
Security section configures database security (not server).

## Distribution configuration reference

Configuration folder (config/) in orient distribution reference:

| file name                     | description           | configured as                          |
|----------|-------------|----------------|
| orientdb-server-config.xml    | Main configuration    | `config` (yaml) or `config-file` (xml) |
| security.json                 | Database security configuration | `security` (yaml) or `security-file` (json) |
| automatic-backup.json         | Backup configuration | Reference to file may be set `OAutomaticBackup` handler property. May be configured directly with handler properties (old way). | 
| hazelcast.xml and default-distributed-db-config.json | Distributed configuration | Requires additional dependency (`orientdb-distributed`). Links to files set in `OHazelcastPlugin` handler properties. |
| orientdb-client-log.properties and orientdb-server-log.properties | Logging configuration | Not needed.

!!! tip
    If you want to replicate orientdb server layout (merge it with your app folder), you can do it like this:

    ```
    APP HOME/
        config/
            config.xml
            security.json
            backup.json
    ```
    
    ```yaml
    orient-server:
        files-path: $APP_HOME
        config-file: $APP_HOME/config/config.xml
        security-file: $APP_HOME/config/security.json
    ```
    
    Correct reference to backup.json set in config.xml (OAutomaticBackup handler configuration).

    Orient will create databases in: `$APP_HOME/databases/name`.

    Of course, config.xml, security.json and backup.json may be configured in yaml. 
    It's just an example to better understand configuration.

## Graph server

By default, server supports document and object databases.
If graph db required you'll need to add graph dependency: `com.orientechnologies:orientdb-graphdb:3.0.25`.

Graph related sections are commented in default [yaml config](default-configs.md#yaml-config):

Enable this section if [gremlin](http://orientdb.com/docs/3.0.x/gremlin/Gremlin.html) support required

```yaml
- clazz: com.orientechnologies.orient.graph.handler.OGraphServerHandler
  parameters:
    - enabled: true
```

Enable this section if [gephi](http://orientdb.com/docs/3.0.x/plugins/Gephi.html) support required (requires `OGraphServerHandler` if gremlin queries used)

!!! note 
    Gremlin has become optional since [2.2.0](https://mvnrepository.com/artifact/com.orientechnologies/orientdb-graphdb/3.0.25). You will have to add an additional explicit dependency to enable Gremlin.

```yaml
pattern: 'GET|gephi/*'
implementation: com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetGephi
```

## Lucene plugin

Orient 2 distribution includes lucene plugin out of the box.
To enable lucene indexes in embedded server add dependency: `com.orientechnologies:orientdb-lucene:3.0.25`.

Plugin will be automatically registered. 

!!! note
    It's actually not an "orient plugin" anymore and so not shown in registered orient plugins (in orient servlet).

Lucene plugin includes dependency on graph, so explicit graph dependency could be avoided.

## ETL

To use [ETL](http://orientdb.com/docs/3.0.x/etl/ETL-Introduction.html)
add dependency `com.orientechnologies:orientdb-etl:3.0.25`

ETL plugin includes dependency on graph, so explicit graph dependency could be avoided.

## Admin servlet

If embedded server is started, special orient info servlet is available in admin context: [http://localhost:8081/orient](http://localhost:8081/orient).
It shows basic info about server configuration, link to embedded studio and links to most useful orient documentation pages.
  
Special url [http://localhost:8081/orient/studio/](http://localhost:8081/orient/studio/) redirects to embedded studio.

Servlet installation may be disabled in configuration:

```yaml
orient-server:
    admin-servlet: false
```
