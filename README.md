#Embedded OrientDB server for dropwizard

[![License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Build Status](http://img.shields.io/travis/xvik/dropwizard-orient-server.svg?style=flat&branch=master)](https://travis-ci.org/xvik/dropwizard-orient-server)
[![Coverage Status](https://img.shields.io/coveralls/xvik/dropwizard-orient-server.svg?style=flat)](https://coveralls.io/r/xvik/dropwizard-orient-server?branch=master)

### About

Simplifies [OrientDB](http://www.orientechnologies.com/orientdb/) usage with [dropwizard](http://dropwizard.io/). 
Best fits for development environment (to easily try orient or simplify developer environment installation), but
may be used in production for small to middle projects.

Features:
* [Embedded orient server](http://www.orientechnologies.com/docs/1.7.8/orientdb.wiki/Embedded-Server.html), 
managed by dropwizard (using [Managed object](http://dropwizard.io/manual/core.html#managed-objects))
* Configuration in main yaml configuration file or with external 
[orient xml configuration](http://www.orientechnologies.com/docs/1.7.8/orientdb.wiki/DB-Server.html#configuration) file
* [Console command](http://www.orientechnologies.com/docs/1.7.8/orientdb.wiki/Console-Commands.html) 
(interactive mode, command execution, commands file execution)
* Embedded [orient studio](http://www.orientechnologies.com/docs/1.7.8/orientdb-studio.wiki/Home-page.html)

### Setup

Releases are published to [bintray jcenter](https://bintray.com/bintray/jcenter) (package appear immediately after release) 
and then to maven central (require few days after release to be published). 

Maven:

```xml
<dependency>
  <groupId>ru.vyarus</groupId>
  <artifactId>dropwizard-orient-server</artifactId>
  <version>1.0.0</version>
</dependency>
```

Gradle:

```groovy
compile 'ru.vyarus:dropwizard-orient-server:1.0.0'
```

### Usage

Configuration class must implement `HasOrientServerConfiguration`:

```java
public class YourConfiguration extends Configuration implements HasOrientServerConfiguration {

    @NotNull
    @Valid
    private OrientServerConfiguration orientServer;

    @Override
    OrientConfiguration getOrientServerConfiguration() {
        return orientServer
    }

    @JsonProperty("orient-server")
    void setOrientServer(OrientServerConfiguration orientServer) {
        this.orientServer = orientServer
    }
}
```

NOTE: It's not required to have not null orient configuration. If `OrientServerConfiguration` is null server will simply not start.

Register orient bundle in application class:

```java
@Override
void initialize(Bootstrap<TestConfiguration> bootstrap) {
    bootstrap.addBundle(new OrientServerBundle(getConfigurationClass()))
}
```

Example application could be found [in tests](https://github.com/xvik/dropwizard-orient-server/blob/master/src/test/groovy/ru/vyarus/dropwizard/orient/support/TestApplication.groovy)

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
Support special placeholder `$TMP`, which is replaced to `java.io.tmpdir`.
* `config` section defines [orient server configuration](http://www.orientechnologies.com/docs/1.7.8/orientdb.wiki/DB-Server.html#configuration).
Orient use xml format for configuration files and this section is simply yaml representation of xml config.
* `config-file` used to specify path to xml configuration file instead of direct yaml configuration in `config` section. 
See [example xml config](https://github.com/xvik/dropwizard-orient-server/blob/master/src/test/resources/ru/vyarus/dropwizard/orient/sample.xml)
(taken from orient distribution)

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

For complete documentation see [orient console wiki](http://www.orientechnologies.com/docs/1.7.8/orientdb.wiki/Console-Commands.html)

### Orient studio

[Orient studio](http://www.orientechnologies.com/docs/1.7.8/orientdb-studio.wiki/Home-page.html) 
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

-
[![Slush java lib generator](http://img.shields.io/badge/Powered%20by-Slush%20java%20lib%20generator-orange.svg?style=flat-square)](https://github.com/xvik/slush-lib-java)