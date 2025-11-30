package ru.vyarus.app.todo;

import jakarta.ws.rs.core.GenericType;
import org.junit.jupiter.api.Test;
import ru.vyarus.app.todo.model.Todo;
import ru.vyarus.app.todo.resource.TodoResource;
import ru.vyarus.dropwizard.guice.test.client.ResourceClient;
import ru.vyarus.dropwizard.guice.test.jupiter.TestDropwizardApp;
import ru.vyarus.dropwizard.guice.test.jupiter.ext.client.rest.WebResourceClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vyacheslav Rusakov
 * @since 11.09.2025
 */
// custom configuration is required because otherwise asset registered on root will collide with default rest path
// alternatively, restMapping = "/rest/"  could be used (if other config fields are not important)
@TestDropwizardApp(value = TodoApp.class,
        // apache client used only because of the PATCH method usage
        apacheClient = true,
        config = "src/test/resources/test-config.yml")
public class IntegrationResourceTest extends AbstractTest {

    @WebResourceClient
    ResourceClient<TodoResource> rest;

    @Test
    void testRest() {

        WHEN("Loading empty list");
        List<Todo> res = rest.get("/", new GenericType<>() {});

        // THEN list is empty
        assertThat(res).isEmpty();

        WHEN("Add element");
        Todo added = rest.post("/", Todo.builder()
                        .title("one")
                        .completed(false)
                        .build(),
                Todo.class);

        // THEN record added
        assertThat(added)
                .isNotNull()
                .extracting("title", "completed")
                .containsExactly("one", false);
        String id1 = added.getId();
        assertThat(id1).isNotEmpty();

        WHEN("Add second element");
        added = rest.post("/", Todo.builder()
                        .title("two")
                        .completed(false)
                        .build(),
                Todo.class);

        // THEN ok
        assertThat(added)
                .isNotNull()
                .extracting("title", "completed")
                .containsExactly("two", false);
        String id2 = added.getId();
        assertThat(id2).isNotEmpty();

        WHEN("Loading not empty list");
        res = rest.get("/", new GenericType<>() {});

        // THEN list is correct
        assertThat(res).hasSize(2)
                .extracting(Todo::getTitle)
                .containsExactly("one", "two");

        WHEN("Loading by ID");
        Todo item = rest.get(id1, Todo.class);

        // THEN loaded
        assertThat(item).isNotNull()
                .extracting("id").isEqualTo(id1);

        WHEN("Updating entity");
        Todo patch = Todo.builder()
                .id(id1)
                .title("one_custom")
                .completed(true)
                .build();
        Todo updated = rest.patch(id1, patch, Todo.class);

        // THEN update ok
        assertThat(updated).isNotNull()
                .extracting("title", "completed")
                .containsExactly("one_custom", true);

        WHEN("Remove record");
        rest.delete(id1);
        res = rest.get("/", new GenericType<>() {});

        // THEN removed
        assertThat(res).hasSize(1)
                .extracting(Todo::getId).containsExactly(id2);

    }
}
