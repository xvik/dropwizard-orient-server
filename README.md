#Embedded OrientDB server for dropwizard

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/xvik/dropwizard-orient-server)
[![License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Build Status](http://img.shields.io/travis/xvik/dropwizard-orient-server.svg?style=flat&branch=master)](https://travis-ci.org/xvik/dropwizard-orient-server)
[![Coverage Status](https://img.shields.io/coveralls/xvik/dropwizard-orient-server.svg?style=flat)](https://coveralls.io/r/xvik/dropwizard-orient-server?branch=master)

### About

Simplifies [OrientDB](http://www.orientechnologies.com/orientdb/) usage with [dropwizard](http://dropwizard.io/). 
Best fits for development environment (to easily try orient or simplify developer environment installation), but
may be used in production for small to middle projects.

Features:
* For orient 2 and dropwizard 0.8
* [Embedded orient server](http://www.orientechnologies.com/docs/last/orientdb.wiki/Embedded-Server.html), 
managed by dropwizard (using [Managed object](http://dropwizard.io/manual/core.html#managed-objects))
* Configuration in main yaml configuration file or with external 
[orient xml configuration](http://www.orientechnologies.com/docs/last/orientdb.wiki/DB-Server.html#configuration) file
* [Console command](http://www.orientechnologies.com/docs/last/orientdb.wiki/Console-Commands.html) 
(interactive mode, command execution, commands file execution)
* Embedded [orient studio](http://www.orientechnologies.com/docs/last/orientdb-studio.wiki/Home-page.html)

### Setup

Releases are published to [bintray jcenter](https://bintray.com/bintray/jcenter) (package appear immediately after release) 
and then to maven central (require few days after release to be published). 

[![Download](https://api.bintray.com/packages/vyarus/xvik/dropwizard-orient-server/images/download.svg) ](https://bintray.com/vyarus/xvik/dropwizard-orient-server/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/dropwizard-orient-server/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/dropwizard-orient-server)

Maven:

```xml
<dependency>
  <groupId>ru.vyarus</groupId>
  <artifactId>dropwizard-orient-server</artifactId>
  <version>1.2.0</version>
</dependency>
```

Gradle:

```groovy
compile 'ru.vyarus:dropwizard-orient-server:1.2.0'
```

For orient 1.x and dropwizard 0.8 use version 1.1.1 (see [old docs](https://github.com/xvik/dropwizard-orient-server/tree/dw-0.8-orient-1.x))

For orient 1.x and dropwizard 0.7 use version 1.1.0 (see [old docs](https://github.com/xvik/dropwizard-orient-server/tree/dw-0.7))

### Usage

Configuration class must implement `HasOrientServerConfiguration`:

```java
public class MyConfiguration extends Configuration implements HasOrientServerConfiguration {

    @NotNull
    @Valid
    private OrientServerConfiguration orientServerConfiguration;

    @Override
    public OrientConfiguration getOrientServerConfiguration() {
        return orientServerConfiguration;
    }

    @JsonProperty("orient-server")
    public void setOrientServer(OrientServerConfiguration orientServerConfiguration) {
        this.orientServerConfiguration = orientServerConfiguration;
    }
}
```

NOTE: It's not required to have not null orient configuration. If `OrientServerConfiguration` is null server will simply not start.

Register orient bundle in application class:

```java
@Override
public void initialize(final Bootstrap<MyConfiguration> bootstrap) {
    bootstrap.addBundle(new OrientServerBundle(getConfigurationClass()));
}
```

Example application could be found [in tests](https://github.com/xvik/dropwizard-orient-server/blob/master/src/test/groovy/ru/vyarus/dropwizard/orient/support/TestApplication.groovy)


##### Client initialization

Server lifecycle is managed using `Managed` object, so embedded server will start only together with jetty (`server` command) or with environment command.
Managed instances are started after all bundles run methods and even after application run method, so server will be unreachable if you try to access
it from these methods.

It's better to do your orient client initialization inside your own `Managed` object, to make sure this logic run after server start.
(for example, when you use remote connection, the only whey to properly check database existence on startup would be using Managed object)

You can use `plocal` connection together with embedded server: plocal doesn't requires started server and it's faster than remote connection,
but with embedded server you would be able to use studio together with your application (win-win).

Server stores database files in '${files-path}/databases' folder, so plocal connection for server managed database would be:

```
plocal:${files-path}/databases/dbname
```
where `${files-path}` should be replaced with path from server configuration and `dbname` is database name.

### Configuration

Define orient section in application config yaml file:

```yaml
orient-server:
  start: true
  files-path: $TMP/db/
  
  config:
    ...
```

You can start with this [configuration file](https://github.com/xvik/dropwizard-orient-server/blob/master/src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml). 

* `start` enables or disables orient server start (the same effect will be if orient configuration section will not exist, 
this option exist to allow disabling server without removing entire config section)
* `files-path` defines folder, where orient will store database files. May be not existent directory - orient will create it when necessary.
Support special placeholder '$TMP', which is replaced to 'java.io.tmpdir'.
Support special placeholder '$JAR', which is replaced by the current jar's directory path - in fat jars this will be the folder containing the fat jar.
* `config` section defines [orient server configuration](http://www.orientechnologies.com/docs/last/orientdb.wiki/DB-Server.html#configuration).
Orient use xml format for configuration files and this section is simply yaml representation of xml config.
* `config-file` used to specify path to xml configuration file instead of direct yaml configuration in 'config' section. 
See [example xml config](https://github.com/xvik/dropwizard-orient-server/blob/master/src/test/resources/ru/vyarus/dropwizard/orient/sample.xml)
(taken from orient distribution)

**IMPORTANT**: user `root` must be defined in configuration, because orient 2 asks for root user password on start, and in embedded mode it can't save it (so will ask on each start).
To avoid this case, error is thrown if no 'root' user defined.

Also, note that server users and database users are different! In default configuration `root` and `guest` users defined for server.
When new database created, orient will create default database users: `admin`, `reader`, `writer`.

#### Graph server

By default, server supports document and object databases.
If graph db required you'll need to add graph dependency: `com.orientechnologies:orientdb-graphdb:2.0.5`.

Graph related sections are commented in default [yaml config](https://github.com/xvik/dropwizard-orient-server/blob/master/src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml):

Enable this section if [gremlin](http://www.orientechnologies.com/docs/last/orientdb.wiki/Gremlin.html) support required

```yaml
clazz: com.orientechnologies.orient.graph.handler.OGraphServerHandler
        parameters:
        ...
```

Enable this section if [gephi](http://www.orientechnologies.com/docs/last/orientdb.wiki/Gephi.html) support required (requires OGraphServerHandler if gremlin queries used)

```yaml
pattern: 'GET|gephi/*'
implementation: com.orientechnologies.orient.graph.server.command.OServerCommandGetGephi
```

If gremlin not used, it's better to remove gremlin dependencies (mainly because of groovy size)

```groovy
compile ("com.orientechnologies:orientdb-graphdb:2.0.5") {
    exclude module: 'gremlin-java'
    exclude module: 'gremlin-groovy'
}
```

#### Lucene plugin

Orient 2 distribution includes lucene plugin out of the box.
To enable lucene indexes in embedded server add dependency: `com.orientechnologies:orientdb-lucene:2.0.5`.

And register lucene plugin in handlers section:

```yaml
handlers:
    - clazz: com.orientechnologies.lucene.OLuceneIndexPlugin
```

[Example](https://github.com/xvik/dropwizard-orient-server/blob/master/src/test/resources/ru/vyarus/dropwizard/orient/yamlLuceneConfig.yml)

This registration is completely equivalent to default lucene plugin registration in orient distribution.

Lucene plugin includes dependency on graph, so explicit graph dependency could be avoided.

#### ETL

To use [ETL](http://www.orientechnologies.com/docs/2.0/orientdb-etl.wiki/Introduction.html)
add dependency `com.orientechnologies:orientdb-etl:2.0.5`

ETL plugin includes dependency on graph, so explicit graph dependency could be avoided.

### Console

Internally bundle registers orient console command (`ConsoleCommand`). Console may be used in interactive mode, to execute command(s) 
or to process commands file.

Console is very efficient for learning orient (playing with queries) and may be used to easily run predefined scripts.

If started without additional parameters, console will be in interactive mode:

```bash
$ [..] console config.yml
```

Where [..] is main class definition (like `java MyApp` or `java -jar app.jar MyApp`) and `config.yml` is your application yaml config.

NOTE: console launching will not start orient server, but you can use it alongside with started application. Also, 
you can use plocal connection to work with db from console even without server (see console output, it will suggest connection commands)

To execute command directly, write it as additional argument:

```bash
$ [..] console config.yaml help
```

This will start console, execute help command and exit. More than one command may be executed (commands must be separated with ';')

And the last option is to launch sql fie, for example commands.sql:

```sql
set echo true;
create database memory:test;
select from OUser;
drop database;
```

```bash
$ [..] console config.yaml commands.sql
```

Will execute all commands in file and exit.
Note that `set echo true` enables additional logs (may be useful for debug). Another useful flag is `set ignoreErrors true`.

For complete documentation see [orient console wiki](http://www.orientechnologies.com/docs/last/orientdb.wiki/Console-Commands.html)

### Orient studio

[Orient studio](http://www.orientechnologies.com/docs/last/orientdb-studio.wiki/Home-page.html)
is irreplaceable tool for both learning and development. You will need it to validate schema, do manual schema changes and migrations, 
debug sql queries (all the things you usually do in external applications like SqlDeveloper for relational databases).

After jetty server start (usual dropwizard startup):

```bash
$ [..] server config.yml
```

Studio will be available on url: [http://localhost:2480/studio/](http://localhost:2480/studio/)

NOTE: Studio will not start if static content listener is not defined in configuration (defined in example configuration)

```yaml
commands:
  - pattern: 'GET|www GET|studio/ GET| GET|*.htm GET|*.html GET|*.xml GET|*.jpeg GET|*.jpg GET|*.png GET|*.gif GET|*.js GET|*.css GET|*.swf GET|*.ico GET|*.txt GET|*.otf GET|*.pjs GET|*.svg'
    implementation: 'com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent'
    parameters:
        - name: 'http.cache:*.htm *.html'
          value: 'Cache-Control: no-cache, no-store, max-age=0, must-revalidate\r\nPragma: no-cache'
        - name: 'http.cache:default'
          value: 'Cache-Control: max-age=120'
```

[Studio github repository](https://github.com/orientechnologies/orientdb-studio).
You can check version, packaged inside jar [here](https://github.com/xvik/dropwizard-orient-server/blob/master/src/main/resources/ru/vyarus/dropwizard/orient/studio/version.txt)

### Might also like

* [dropwizard-guicey](https://github.com/xvik/dropwizard-guicey) - dropwizard guice integration
* [guice-persist-orient](https://github.com/xvik/guice-persist-orient) - guice integration for orient

-
[![java lib generator](http://img.shields.io/badge/Powered%20by-%20Java%20lib%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-lib-java)
