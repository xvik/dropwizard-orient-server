package ru.vyarus.app.todo.resource;

import com.google.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import ru.vyarus.app.todo.model.Todo;
import ru.vyarus.app.todo.repository.TodoRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vyacheslav Rusakov
 * @since 11.09.2025
 */
@Path("/todo")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class TodoResource {

    @Inject
    TodoRepository repo;

    @GET
    public Collection<Todo> get() {
        List<Todo> res = repo.getAllAsList().stream()
                .map(todo -> processId(repo.detach(todo)))
                .collect(Collectors.toList());
        return res;
    }

    @GET
    @Path("{id}")
    public Todo getById(@PathParam("id") String id) {
        // unproxied object is required
        return processId(repo.detach(repo.get(recoverId(id))));
    }

    @POST
    public Todo addTodos(Todo todo) {
        log.info("Adding todo: {}", todo.getTitle());
        todo.setCompleted(false);
        return processId(repo.detach(repo.save(todo)));
    }

    @DELETE
    public void delete() {
        log.info("Deleting all todos");
        repo.deleteAll();
    }

    @DELETE
    @Path("{id}")
    public void deleteById(@PathParam("id") String id) {
        log.info("Deleting tod {}", id);
        repo.delete(recoverId(id));
    }

    @PATCH
    @Path("{id}")
    public Todo edit(@PathParam("id") String id, Todo patch) {
        log.info("Modifying todo {}", id);
        // detach required because outside of transaction
        Todo todo = repo.detach(repo.get(recoverId(id)));
        todo.update(patch);
        return processId(repo.detach(repo.save(todo)));
    }

    private Todo processId(Todo todo) {
        todo.setId(todo.getId().replace("#", ""));
        return todo;
    }

    private void recoverId(Todo todo) {
        todo.setId(recoverId(todo.getId()));
    }

    private String recoverId(String id) {
        return id == null || id.startsWith("#") ? id : "#" + id;
    }
}
