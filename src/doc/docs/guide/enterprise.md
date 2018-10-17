# Enterprise edition

Orient has three distributions:

* Community edition
* [Enterprise edition](http://orientdb.com/orientdb-enterprise/)
* Workbench

All three are different only in plugins (see plugins folder in distributions):

* Community: studio and lucene
* Enterprise: studio, lucene and orient agent
* Workbench: workbench

Orient agent from enterprise edition is required for sql profiler and workbench application.

Workbench allows you to easily monitor orient server metrics (and before 2.1 workbench was showing profiler data).

!!! note
    Enterprise edition is free for development, so you will need it for **sql profiler** during development.

### Installing ee features into embedded server

You can combine everything in one embedded server simply by putting all plugins in there.

First, [enable dynamic plugins](Orient-plugins).

```yaml
properties:
       - name: plugin.dynamic
         value: true
       - name: plugin.hotReload
         value: false
```

Create `plugins` directory in the configured `files-path` folder.

Copy plugins from [orient distribution](http://orientdb.com/orientdb-enterprise/) into `plugins` folder and they will be installed on server startup.

Note that it's better to [install lucene plugin as dependency](https://github.com/xvik/dropwizard-orient-server#lucene-plugin), because it will be easier for final delivery.

!!! important
    If agent plugin is installed, jmx plugin must be configured like this:

    ```yaml
    handlers:
       - clazz: com.orientechnologies.orient.server.handler.OJMXPlugin
         parameters:
                - name: enabled
                  value: false
                - name: profilerManaged
                  value: true
    ```

### Manual agent installation

If you have enterprise license and want to include agent into your app distribution (instead of copying it manually on each environment):

* create libs folder in your project and move agent plugin into it (e.g. orientdb-enterprise-2.0.12\plugins\agent-2.0.12.zip)
* rename it from .zip to .jar
* in project add dependency for libs folder. For example, in gradle:
```groovy
runtime fileTree(dir: 'libs', include: '*.jar')
```
* change  handlers section in config (add agent and change jmx parameters):
```yaml
handlers:
   - clazz: com.orientechnologies.agent.OEnterpriseAgent
     parameters:
           - name: license
             value: '@LICENSE@'
   - clazz: com.orientechnologies.orient.server.handler.OJMXPlugin
     parameters:
           - name: enabled
             value: false
           - name: profilerManaged
             value: true
```