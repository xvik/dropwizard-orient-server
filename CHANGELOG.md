* Update orient 2.2.17 -> 2.2.26
* Update dropwizard 1.0.6 -> 1.1.4
* /orient servlet indicates enabled https and (or) ssl (for binary)
* Rewrite relative keystore paths in orient sockets config (OServerTLSSocketFactory) to absolute paths relative to application startup dir (if file exists). 
  By default orient resolve keystore locations from ORIENTDB_HOME, but most likely, orient and dropwizard will be both 
  configured to use ssl and it may be handy to refer to the same keystore relatively.
* /orient/studio redirect to https when orient is configured with https for http listener (#7)   

### 2.0.0 (2017-02-25)
* Update orient 2.2.10 -> 2.2.17
* Update dropwizard 1.0.2 -> 1.0.6
* (breaking) Support official studio jar instead of webjar (not supported anymore!). Studio version not shown anymore in orient servlet. (#4)
* Simplify configuration: allow writing name and value properties as one (name: value). Affects properties and parameters sections for 
commands and handlers and protocols section
* Add orient specify configuration (security.json) support in yaml (security property) or as path to file (security-file property).
* Path configuration properties "files-path", "config-file" and "security-file" may use system (or environment)
properties with ${prop} syntax. Also special properties $TMP (java.io.tmpdir alias), $FILES_HOME (files-path property value) 
and $APP_HOME (application start directory) are allowed.

NOTE: 

* lucene is not orient plugin anymore and so does not require extra configuration
* configuration samples were updated according to the latest orient configs and using new simplified properties format 

### 1.4.0 (2016-09-30)
* Update to orient 2.2 (fix compatibility)
* Update to dropwizard 1.0

### 1.3.0 (2015-08-31)
* Orient studio removed from jar: external optional [studio webjar](https://github.com/webjars/orientdb-studio) used instead
* Add orient info servlet installed in admin context (`/orient`). Special url `/orient/studio` redirects to embedded studio (automatically resolve correct port from configuration)

### 1.2.0 (2015-03-19)
* Update orient (1.7.10 -> 2.0.5)
* Update studio
* Update example configurations
* Add validation for root user presence in configuration: otherwise orient would ask for password on each start
* Health check now checks that server is active and storages available (memory check removed because its not available in orient 2)
* Fix console command help (clean urls)

### 1.1.1 (2014-11-25)
* Update dropwizard (0.7 -> 0.8.rc1)
* Update orient (1.7.9 -> 1.7.10)
* Drop java 1.6 support

### 1.1.0 (2014-10-06)
* Update orient (1.7.8 > 1.7.9) - [important hotfix](https://groups.google.com/forum/#!topic/orient-database/vPF85I5Blts)
* Add health check

### 1.0.1 (2014-09-05)
* Avoid print server config users in console help
* Fix pom (fix scope of dropwizard-test dependency)

### 1.0.0 (2014-08-25)
* Initial release