# Welcome to dropwizard-orient-server

!!! summary ""
    Embedded [OrientDB](https://orientdb.dev/) server for [Dropwizard](https://dropwizard.io).

[Release notes](about/release-notes.md) - [Support](about/support.md) - [License](about/license.md)

Simplifies development environment (no need to maintain separate server) without sacrificing functionality (embedded server is 100% the same as usual server).
Also, simplifies production deployment and allows to slightly reduce memory consumption on server due to single (shared) vm.
To switch application to external server simply switch off embedded server in configuration.

Embedding is [officially proposed](https://orientdb.dev/docs/3.2.x/internals/Embedded-Server.html)

!!! note
    Orient 3.2 docs show the following code snippet which could be confusing:
    ```java
    OrientDB orientDB = new OrientDB("embedded:/tmp/",OrientDBConfig.defaultConfig());
    ```
    This is not the same as starting [embedded server](https://orientdb.dev/docs/3.2.x/internals/Embedded-Server.html)!
    The code above only allows using orient databases within application, but not running studio,
    starting rest api, enabling plugins etc.    
    

## Main features

* For orient 3.2 and dropwizard 5
* [Embedded orient server](https://orientdb.dev/docs/3.2.x/internals/Embedded-Server.html), 
managed by dropwizard (using [Managed object](https://www.dropwizard.io/en/release-2.0.x/manual/core.html#managed-objects))
* Configuration in main yaml configuration file or with external 
[orient xml configuration](https://orientdb.dev/docs/3.2.x/internals/DB-Server.html) file
* [Console command](https://orientdb.dev/docs/3.2.x/console/Console-Commands.html) 
(interactive mode, command execution, commands file execution)
* Optional embedded [orient studio](https://orientdb.dev/docs/3.2.x/studio/Studio-Introduction.html)
* Full support of [orient plugins](guide/plugins.md) (and [enterprise edition](guide/enterprise.md))
* [SSL configuration helpers](guide/ssl.md)
