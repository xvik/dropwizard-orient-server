# Enterprise edition

Orient [Enterprise edition](http://orientdb.com/orientdb-enterprise/) is actually community edition + agent plugin
(if you fill in [the form](http://orientdb.com/orientdb-enterprise/) it will simply suggest you to download agent jar).

!!! tip
    Some security features [could be enabled](security.md) in community edition

This enterprise agent activates some hidden abilities like sql profiler.

### Dynamic agent installation

Copy agent jar into [plugins directory](plugins.md).

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

This could be useful for temporary agent usage (not in production). 

### Manual agent installation

If you have enterprise license and want to include agent into your app distribution (instead of copying it manually on each environment):

* create libs folder in your project and move agent plugin into it
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