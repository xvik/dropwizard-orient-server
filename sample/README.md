# Sample Todo App

Simple application showing embedded server usage.

Based on https://todobackend.com/
Client based on: https://todomvc.com/ (ES5 https://todomvc.com/examples/javascript-es5/dist/)

Client is using [guice-persist-orient](https://github.com/xvik/guice-persist-orient) and [dropwizard-guicey](https://github.com/xvik/dropwizard-guicey)
Object Orient API is used.

## Setup

Database must be created before application startup. There is a special command to create
a new database (`CreateDatabaseCommand`). Run (could be done with IDE run configuration)

```
ru.vyarus.app.todo.TodoApp db-create config.yml
```

This command would create plocal database under `orient-server.files-path` directory
(by default, /tmp/db/).

Database name configured in `db.uri` (sample). User credentials:
`db.user`, `db.password`.

NOTE: since orient 3.2 default users (like admin/admin) not created by default, so
command will create required user.

NOTE2: you nay find another user configuration `orient-server.config.users` - these are
server users - it would be required to access server UI.

## Run

Run application (create IDE run configuration):

```
ru.vyarus.app.todo.TodoApp server config.yml
```

* Application is available on http://localhost:9090/  
* Summary servlet: http://localhost:9090/admin/orient
* Server UI (studio): http://localhost:2480/studio/

Use root/root user to login into server.

## Plocal db

Note that you need to use remote database when server is started `uri: 'remote:localhost/sample'`
because server locks all databases.

But, if you're not using the server (`orient-server.start: false`), then 
database could be accessed directly: `uri: 'plocal:/tmp/db/databases/sample'`

## Implementation details

### Config db

`DbModule` configures database integration:

```java
public class DbModule extends DropwizardAwareModule<TodoConfig> {

    @Override
    protected void configure() {
        TodoConfig.DbConfiguration db = configuration().getDb();
        final OrientModule orient = new OrientModule(db.getUri(), db.getUser(), db.getPass());
        // enable default users creation for memory db (for tests)
        // real database users would be created either manually or in DbLifecycle
        if (DBUriUtils.isMemory(db.getUri())) {
            log.info("Default users creation enabled for memory database: {}", db.getUri());
            orient.withConfig(OrientDBConfig.builder()
                    .addConfig(OGlobalConfiguration.CREATE_DEFAULT_USERS, true)
                    .build());
        }
        install(orient);
        install(new AutoScanSchemeModule(appPackage() + ".model"));
        install(new RepositoryModule());
    }
}
```

As it was mentioned before, since 3.2 orient does not create default users.
But this is not good for tests when a new memory database is created for each test, so
module reverts 3.1 behaviour for in-memory databases `if (DBUriUtils.isMemory(db.getUri())) {`

There is only one model class: Todo. 

```java
install(new AutoScanSchemeModule(appPackage() + ".model"));
```

Would find it and create database schema, based on it (if there were other classes - they would also be processed).

Note special fields:

```java
    @Id
    private String id;
    @Version
    private Long version;
```

Id for ID mapping (could also be ORid class instead of string),
Version - optimistic locking number (same as in hibernate).

Repository module required for spring-data like repositories support:

```java
install(new RepositoryModule());
```

There is only one reposiotry:

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface TodoRepository extends ObjectCrud<Todo> {

    @Query("delete from Todo")
    void deleteAll();

}
```

Most crud operations implemented in [ObjectCrud](https://xvik.github.io/guice-persist-orient/5.0.0/repository/mixin/objcrud/)

```java
@ProvidedBy(DynamicSingletonProvider.class)
```

Is required for cheating guice which does not allow bindings without annotations, so
this provider would auto create a class implementing interface (at runtime).

Also note that transaction scope is on repository class (`@Transactional`)

### Db startup

Guice module does not know when database must be started, so there is special `Managed` object
to start/stop database:

```java
@Slf4j
public class DbLifecycle implements Managed {

    @Inject
    private DatabaseManager orientService;
    @Inject
    @Config
    private TodoConfig.DbConfiguration config;

    @Override
    public void start() throws Exception {
        orientService.start();
    }

    @Override
    public void stop() throws Exception {
        orientService.stop();
    }
}
```

### Resource

For simplicity, database model class `Todo` is used directly in REST: `TodoResource`.

But, orient object api returns a proxy object (like hibernate), which can't be serialized directly,
so we have to detach it before sending: `repo.detach(todo)` (unproxy).

Also, orient id looks like "#12:1" which is not very good fit for use in REST urls,
so resource have to remove "#" each time it sends data to client and put back "#" when performing
db operations.