# Orient server plugins

Orient has some [plugins](https://orientdb.org/docs/3.0.x/plugins/Extend-Server.html) by default (mail, backup, jmx etc) and few external plugins (studio, lucene, etc).
With usual orient server, external plugins are simply put into `$DISTRIBUTION_HOME/plugins` folder. Orient monitors this folder and install plugins. If plugin contains only static files (studio) it is registered as virtual folder in server. If plugin requires integration, it is registered as handler.

So basically, manual plugin registration with handlers configuration is the same as using plugins folder (except static plugins, which may be registered only from plugins folder).

With embedded server, you can use both dynamic plugins and direct configuration.

## Dynamic plugins

Dynamic plugins are enabled by default, but without hot-reloading. You can use the following properties to change it:

```yaml
properties:
   - name: plugin.dynamic
     value: true
   - name: plugin.hotReload
     value: false
```

`plugin.hotReload` may be enabled to support hot plugin installation (when plugin is copied into plugins folder during runtime)

When `plugin.dynamic` is true, you can create `plugins` folder inside your `files-path` folder and put plugins there. 
It is useful for environment-specific plugins installation (e.g. install studio like plugin and not with webjar).

!!! warning
    Orient will show warnings about these properties

## Static plugins

Static plugins are enabled by adding (if required) plugin jar into classpath and adding new handler.

For example, for lucene plugin:

!!! important
    Lucene plugin is not activated like this anymore. It's just as an example:

```groovy
com.orientechnologies:orientdb-lucene:2.0.5
```

And enabling in configuration

```yaml
handlers:
    - clazz: com.orientechnologies.lucene.OLuceneIndexPlugin
```

Read more about plugins in [orient docs](https://orientdb.org/docs/3.0.x/plugins/Extend-Server.html)