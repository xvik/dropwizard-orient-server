# Embedded OrientDB server for dropwizard

[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Build Status](https://img.shields.io/travis/xvik/dropwizard-orient-server.svg?style=flat&branch=master)](https://travis-ci.org/xvik/dropwizard-orient-server)
[![Appveyor build status](https://ci.appveyor.com/api/projects/status/github/xvik/dropwizard-orient-server?svg=true)](https://ci.appveyor.com/project/xvik/dropwizard-orient-server)
[![codecov](https://codecov.io/gh/xvik/dropwizard-orient-server/branch/master/graph/badge.svg)](https://codecov.io/gh/xvik/dropwizard-orient-server)

**DOCUMENTATION** https://xvik.github.io/dropwizard-orient-server

### About

Embeds [OrientDB](http://orientdb.com/orientdb/) server into [dropwizard](http://dropwizard.io/).
 
Simplifies development environment (no need to maintain separate server) without sacrificing functionality (embedded server is 100% the same as usual server).
Also, simplifies production deployment and allows to slightly reduce memory consumption on server due to single (shared) vm.
To switch application to external server simply switch off embedded server in configuration.

Embedding is [officially proposed](https://orientdb.com/database/orientdb-embedded/)  

Features:
* For orient 3.0 and dropwizard 2.0.0
* [Embedded orient server](http://orientdb.com/docs/3.0.x/internals/Embedded-Server.html), 
managed by dropwizard (using [Managed object](https://www.dropwizard.io/en/release-2.0.x/manual/core.html#managed-objects))
* Configuration in main yaml configuration file or with external 
[orient xml configuration](http://orientdb.com/docs/3.0.x/internals/DB-Server.html) file
* [Console command](http://orientdb.com/docs/3.0.x/console/Console-Commands.html) 
(interactive mode, command execution, commands file execution)
* Optional embedded [orient studio](http://orientdb.com/docs/3.0.x/studio/Studio-Introduction.html)
* Full support of [orient plugins](http://xvik.github.io/dropwizard-orient-server/3.0.0/plugins/) (and [enterprise edition](http://xvik.github.io/dropwizard-orient-server/2.2.0/enterprise/))
* [SSL configuration helpers](http://xvik.github.io/dropwizard-orient-server/3.0.0/ssl/)

### Setup

Releases are published to [bintray jcenter](https://bintray.com/bintray/jcenter) (package appear immediately after release) 
and then to maven central (require few days after release to be published). 

[![JCenter](https://img.shields.io/bintray/v/vyarus/xvik/dropwizard-orient-server.svg?label=jcenter)](https://bintray.com/vyarus/xvik/dropwizard-orient-server/_latestVersion)
[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/dropwizard-orient-server.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/dropwizard-orient-server)

Maven:

```xml
<dependency>
  <groupId>ru.vyarus</groupId>
  <artifactId>dropwizard-orient-server</artifactId>
  <version>3.0.0</version>
</dependency>
```

Gradle:

```groovy
compile 'ru.vyarus:dropwizard-orient-server:3.0.0'
```

OrientDB | Dropwizard | dropwizard-orient-server
----------|---|------
3.0 | 2.0.0 | [3.0.0](http://xvik.github.io/dropwizard-orient-server/3.0.0) |
3.0 | 1.3.5 | [2.2.0](http://xvik.github.io/dropwizard-orient-server/2.2.0) | 
 2.2.17 |  1.1.4 | [2.1.0](http://xvik.github.io/dropwizard-orient-server/2.1.0)
2.2.0 - 2.2.17 (studio webjar, lucene as plugin) | 1.0 | [1.4.0](https://github.com/xvik/dropwizard-orient-server/tree/1.4.0)
2.0 - 2.1 | 0.8 - 1.0 | [1.3.0](https://github.com/xvik/dropwizard-orient-server/tree/1.3.0)
1.0 | 0.8 | [1.1.1](https://github.com/xvik/dropwizard-orient-server/tree/dw-0.8-orient-1.x)
1.0 | 0.7 | [1.1.0](https://github.com/xvik/dropwizard-orient-server/tree/dw-0.7)

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
