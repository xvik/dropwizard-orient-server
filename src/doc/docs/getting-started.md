# Getting started

## Installation

Releases are published to [bintray jcenter](https://bintray.com/bintray/jcenter) (package appear immediately after release) 
and then to maven central (require few days after release to be published). 

[![JCenter](https://api.bintray.com/packages/vyarus/xvik/dropwizard-orient-server/images/download.svg)](https://bintray.com/vyarus/xvik/dropwizard-orient-server/_latestVersion)
[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/dropwizard-orient-server.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/dropwizard-orient-server)

Maven:

```xml
<dependency>
  <groupId>ru.vyarus</groupId>
  <artifactId>dropwizard-orient-server</artifactId>
  <version>2.1.0</version>
</dependency>
```

Gradle:

```groovy
compile 'ru.vyarus:dropwizard-orient-server:2.1.0'
```

Version works with orient 2.2.26 (and above) and dropwizard 1.1.4 (and above).
For other versions see [compatibility matrix](about/compatibility.md). 

## Usage

Configuration class must implement `HasOrientServerConfiguration`:

```java
public class MyConfiguration extends Configuration implements HasOrientServerConfiguration {

    @NotNull
    @Valid
    private OrientServerConfiguration orientServerConfiguration;

    @Override
    public OrientServerConfiguration getOrientServerConfiguration() {
        return orientServerConfiguration;
    }

    @JsonProperty("orient-server")
    public void setOrientServer(OrientServerConfiguration orientServerConfiguration) {
        this.orientServerConfiguration = orientServerConfiguration;
    }
}
```

!!! note
    It's not required to have not null orient configuration. If `OrientServerConfiguration` is null orient server will simply not start.

Register orient bundle in application class:

```java
@Override
public void initialize(final Bootstrap<MyConfiguration> bootstrap) {
    bootstrap.addBundle(new OrientServerBundle(getConfigurationClass()));
}
```

### Configuration

You can start with the [default configuration](guide/default-configs.md#yaml-config).

Read more about [configuration](guide/configuration.md). 

#### Plugins

You can also enable [orient plugins](https://github.com/xvik/dropwizard-orient-server/wiki/Orient-plugins):
    
- [studio](guide/studio.md)
- [lucene](guide/configuration.md#lucene-plugin)
- [etl](guide/configuration.md#etl)
- [graph server](guide/configuration.md#graph-server) 

!!! note 
    Plugins could be loaded [dynamically or statically](guide/plugins.md).

You may also need plugins from [enterprise edition](guide/enterprise.md) in order to use
query profiler.

### Client initialization

Server lifecycle is managed using a `Managed` object, so the embedded server will start only together with jetty (`server` command).
Managed instances are started after all bundles run methods and even after application run method, so server will be unreachable if you try to access it from these methods.

#### Connecting using plocal

The most efficient way to connect to the embedded database is using `plocal`. This engine writes to the file system to store data. There is a LOG of changes to restore the storage in case of a crash. 

`plocal` doesn't require a started server and it's faster than remote connection. With an embedded server you would be able to use studio (remote connection) together with your application using `plocal` (win-win).

Server stores database files in `${files-path}/databases` folder, so plocal connection for server managed database would be:

```
plocal:${files-path}/databases/dbname
```

where `${files-path}` should be replaced with path from server configuration and `dbname` is database name.

#### Connecting using sockets

If you want to connect to your embedded instance over sockets, it's best to have your orient client initialization inside your own `Managed` object. This is the best way to make sure the connection logic is run after server start.

### Console

You can also access embedded orient with console. This could be used for first initialization
(to run initialization sql scripts). Read more about [console usage](guide/console.md).

### Admin servlet

The state of embedded orient server could be seen in admin servlet:
[http://localhost:8081/orient](http://localhost:8081/orient).

[http://localhost:8081/orient/studio/](http://localhost:8081/orient/studio/) redirects to embedded studio (if deployed).

Could [be disabled](guide/configuration.md#admin-servlet) if required.

