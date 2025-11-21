# Embedded OrientDB server for dropwizard

[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![CI](https://github.com/xvik/dropwizard-orient-server/actions/workflows/CI.yml/badge.svg)](https://github.com/xvik/dropwizard-orient-server/actions/workflows/CI.yml)
[![Appveyor build status](https://ci.appveyor.com/api/projects/status/github/xvik/dropwizard-orient-server?svg=true)](https://ci.appveyor.com/project/xvik/dropwizard-orient-server)
[![codecov](https://codecov.io/gh/xvik/dropwizard-orient-server/branch/master/graph/badge.svg)](https://codecov.io/gh/xvik/dropwizard-orient-server)

**DOCUMENTATION** https://xvik.github.io/dropwizard-orient-server

### About

Embeds [OrientDB](https://orientdb.dev) server into [dropwizard](https://dropwizard.io/).
 
Simplifies development environment (no need to maintain separate server) without sacrificing functionality (embedded server is 100% the same as usual server).
Also, simplifies production deployment and allows to slightly reduce memory consumption on server due to single (shared) vm.
To switch application to external server simply switch off embedded server in configuration.

Embedding is [officially proposed](https://orientdb.dev/docs/3.2.x/internals/Embedded-Server.html)  

Features:
* For orient 3.2 and dropwizard 4 - 5
* [Embedded orient server](https://orientdb.dev/docs/3.2.x/internals/Embedded-Server.html), 
managed by dropwizard (using [Managed object](https://www.dropwizard.io/en/release-2.0.x/manual/core.html#managed-objects))
* Configuration in main yaml configuration file or with external 
[orient xml configuration](https://orientdb.dev/docs/3.2.x/internals/DB-Server.html) file
* [Console command](https://orientdb.dev/docs/3.2.x/console/Console-Commands.html) 
(interactive mode, command execution, commands file execution)
* Optional embedded [orient studio](https://orientdb.dev/docs/3.2.x/studio/Studio-Introduction.html)
* Full support of [orient plugins](http://xvik.github.io/dropwizard-orient-server/latest/plugins/) (and [enterprise edition](http://xvik.github.io/dropwizard-orient-server/2.2.0/enterprise/))
* [SSL configuration helpers](http://xvik.github.io/dropwizard-orient-server/latest/ssl/)

### The state of OrientDB

OrientDB was bought by SAP, which stopped paid support but continued supporting a tiny team
working on it. As a consequence, previously enterprise features (profiler) are open-sourced now.

**OrientDB is alive**: bugfix releases are made [every few months](https://github.com/orientechnologies/orientdb/releases),
new features are added (very slowly).
[OrientDB 4](https://github.com/orientechnologies/orientdb/discussions/10339) is also in work (will not release soon).

The original author of OrientDB Luca Garulli [has started](https://github.com/orientechnologies/orientdb/issues/9734) a
new db https://arcadedb.com/ (only query engine was forked - overall it's a different project)

In 2024, one of the core developers Andrii Lomakin has forked orient as https://youtrackdb.io/.
Work in progress, no releases yet but looks very promising - this db should be
[used inside](https://youtrack.jetbrains.com/articles/YTDB-A-3/Project-roadmap) JetBrains YouTrack

### Setup

[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/dropwizard-orient-server.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/dropwizard-orient-server)

Maven:

```xml
<dependency>
  <groupId>ru.vyarus</groupId>
  <artifactId>dropwizard-orient-server</artifactId>
  <version>4.0.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'ru.vyarus:dropwizard-orient-server:4.0.0'
```

OrientDB | Dropwizard | dropwizard-orient-server                                                         
----------|------------|----------------------------------------------------------------------------------
3.2 | 4 - 5      | [4.0.0](http://xvik.github.io/dropwizard-orient-server/4.0.0)                    |
3.0 - 3.2 | 2.0.0      | [3.0.0](http://xvik.github.io/dropwizard-orient-server/3.0.0)                    |
3.0 | 1.3.5      | [2.2.0](http://xvik.github.io/dropwizard-orient-server/2.2.0)                    | 
 2.2.17 | 1.1.4      | [2.1.0](http://xvik.github.io/dropwizard-orient-server/2.1.0)                    
2.2.0 - 2.2.17 (studio webjar, lucene as plugin) | 1.0        | [1.4.0](https://github.com/xvik/dropwizard-orient-server/tree/1.4.0)             
2.0 - 2.1 | 0.8 - 1.0  | [1.3.0](https://github.com/xvik/dropwizard-orient-server/tree/1.3.0)             
1.0 | 0.8        | [1.1.1](https://github.com/xvik/dropwizard-orient-server/tree/dw-0.8-orient-1.x) 
1.0 | 0.7        | [1.1.0](https://github.com/xvik/dropwizard-orient-server/tree/dw-0.7)            

#### Snapshots

Snapshots could be used through JitPack:

* Go to [JitPack project page](https://jitpack.io/#ru.vyarus/dropwizard-orient-server)
* Select `Commits` section and click `Get it` on commit you want to use (you may need to wait while version builds if no one requested it before)
* Follow displayed instruction: 
    - Add jitpack repository: `maven { url 'https://jitpack.io' }`
    - Use commit hash as version: `ru.vyarus:dropwizard-orient-server:378ece3c6e`


### Usage

Read [documentation](https://xvik.github.io/dropwizard-orient-server/)

### Might also like

* [dropwizard-guicey](https://github.com/xvik/dropwizard-guicey) - dropwizard guice integration
* [guice-persist-orient](https://github.com/xvik/guice-persist-orient) - guice integration for orient

---
[![java lib generator](http://img.shields.io/badge/Powered%20by-%20Java%20lib%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-lib-java)
