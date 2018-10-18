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
* For orient 2.2.26 and dropwizard 1.1.4
* [Embedded orient server](http://orientdb.com/docs/last/Embedded-Server.html), 
managed by dropwizard (using [Managed object](http://www.dropwizard.io/1.0.2/docs/manual/core.html#managed-objects))
* Configuration in main yaml configuration file or with external 
[orient xml configuration](http://orientdb.com/docs/last/DB-Server.html) file
* [Console command](http://orientdb.com/docs/last/Console-Commands.html) 
(interactive mode, command execution, commands file execution)
* Optional embedded [orient studio](http://orientdb.com/docs/last/Studio-Home-page.html)
* Full support of [orient plugins](https://github.com/xvik/dropwizard-orient-server/wiki/Orient-plugins) (and [enterprise edition](https://github.com/xvik/dropwizard-orient-server/wiki/Enterprise-edition))
* [SSL configuration helpers](https://github.com/xvik/dropwizard-orient-server/wiki/SSL)

### Setup

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

OrientDB | Dropwizard | dropwizard-orient-server
----------|---|------
&gt;= 2.2.17 | &gt;= 1.1.4 | [2.1.0](http://xvik.github.io/dropwizard-orient-server/2.1.0)
&lt; 2.2.17 (studio webjar, lucene as plugin) | 1.0 | [1.4.0](https://github.com/xvik/dropwizard-orient-server/tree/1.4.0)
2.0 - 2.1 | 0.8 - 1.0 | [1.3.0](https://github.com/xvik/dropwizard-orient-server/tree/1.3.0)
1.0 | 0.8 | [1.1.1](https://github.com/xvik/dropwizard-orient-server/tree/dw-0.8-orient-1.x)
1.0 | 0.7 | [1.1.0](https://github.com/xvik/dropwizard-orient-server/tree/dw-0.7)

##### Snapshots

You can use snapshot versions through [JitPack](https://jitpack.io):

* Go to [JitPack project page](https://jitpack.io/#xvik/dropwizard-orient-server)
* Select `Commits` section and click `Get it` on commit you want to use (top one - the most recent)
* Follow displayed instruction: add repository and change dependency (NOTE: due to JitPack convention artifact group will be different)

### Usage

Read [documentation](https://xvik.github.io/dropwizard-orient-server/)

### Might also like

* [dropwizard-guicey](https://github.com/xvik/dropwizard-guicey) - dropwizard guice integration
* [guice-persist-orient](https://github.com/xvik/guice-persist-orient) - guice integration for orient

---
[![java lib generator](http://img.shields.io/badge/Powered%20by-%20Java%20lib%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-lib-java)
