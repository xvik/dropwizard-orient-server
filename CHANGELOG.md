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