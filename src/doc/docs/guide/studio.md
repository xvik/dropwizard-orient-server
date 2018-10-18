# Orient studio

[Orient studio](http://orientdb.com/docs/3.0.x/studio/Studio-Introduction.html)
is irreplaceable tool for both learning and development. You will need it to validate schema, do manual schema changes and migrations, 
debug sql queries (all the things you usually do in external applications like SqlDeveloper for relational databases).

Studio could be embedded using official orient jar (by default, it is not included).

Add dependency:

```groovy
compile 'com.orientechnologies:orientdb-studio:3.0.9'
```

After jetty server start (usual dropwizard startup):

```bash
$ [..] server config.yml
```

Studio will be available on url: [http://localhost:2480/studio/](http://localhost:2480/studio/). 
Port number depends on orient configuration. You can use universal url in admin servlet: [http://localhost:8081/orient/studio/](http://localhost:8081/orient/studio/),
 which will redirect to actual studio location.

!!! note 
    Studio will not start if static content listener is not defined in configuration (defined in example configuration)

    ```yaml
    commands:
      - pattern: 'GET|www GET|studio/ GET| GET|*.htm GET|*.html GET|*.xml GET|*.jpeg GET|*.jpg GET|*.png GET|*.gif GET|*.js GET|*.css GET|*.swf GET|*.ico GET|*.txt GET|*.otf GET|*.pjs GET|*.svg'
        implementation: 'com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent'
        parameters:
            - http.cache:*.htm *.html: 'Cache-Control: no-cache, no-store, max-age=0, must-revalidate\r\nPragma: no-cache'
            - http.cache:default: 'Cache-Control: max-age=120'
    ```

[Studio github repository](https://github.com/orientechnologies/orientdb-studio).
