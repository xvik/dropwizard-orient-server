# Console

Internally bundle registers orient console command (`ConsoleCommand`). Console may be used in interactive mode, to execute command(s) 
or to process commands file.

Console is very efficient for learning orient (playing with queries) and may be used to easily run predefined scripts.

If started without additional parameters, console will be in interactive mode:

```bash
$ [..] console config.yml
```

Where [..] is main class definition (like `java MyApp` or `java -jar app.jar MyApp`) and `config.yml` is your application yaml config.

!!! note
    Console launching will not start orient server, but you can use it alongside with started application. Also, 
    you can use plocal connection to work with db from console even without server (see console output, it will suggest connection commands)

To execute command directly, write it as additional argument:

```bash
$ [..] console config.yaml help
```

This will start console, execute help command and exit. More than one command may be executed (commands must be separated with ';')

And the last option is to launch sql fie, for example commands.sql:

```sql
set echo true;
create database memory:test;
select from OUser;
drop database;
```

```bash
$ [..] console config.yaml commands.sql
```

Will execute all commands in file and exit.
Note that `set echo true` enables additional logs (may be useful for debug). Another useful flag is `set ignoreErrors true`.

For complete documentation see [orient console doc](https://orientdb.dev/docs/3.2.x/console/Console-Commands.html)
