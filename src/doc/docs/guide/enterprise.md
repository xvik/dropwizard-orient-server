# Enterprise edition

!!! important
    Enterprise edition is [free and open source now](https://orientdb.dev/news/enterprise-agent-open-source/).

Orient [Enterprise edition](http://orientdb.com/orientdb-enterprise/) is actually community edition + agent plugin

Every orientdb [github release](https://github.com/orientechnologies/orientdb/releases/tag/3.2.46) contains a link to agent.jar
Just download it and put it into  [plugins directory](plugins.md).

!!! warning
    There is also a maven central dependency: `com.orientechnologies:agent:3.2.46`,
    but it **can't be used directly** because agent.jar contains embedded slf4j - 
    dropwizard will simply not start

!!! tip
    Some security features [could be enabled](security.md) in community edition

This enterprise agent activates some hidden abilities like sql profiler.

### Dynamic agent installation

You can just drop agent.jar into  [plugins directory](plugins.md)

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

### Agent configuration

Change handlers section in config (add agent and change jmx parameters):

```yaml
handlers:
   - clazz: com.orientechnologies.agent.OEnterpriseAgent
   - clazz: com.orientechnologies.orient.server.handler.OJMXPlugin
     parameters:
           - name: enabled
             value: false
           - name: profilerManaged
             value: true
```