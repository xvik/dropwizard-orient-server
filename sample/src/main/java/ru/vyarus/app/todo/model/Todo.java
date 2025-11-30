package ru.vyarus.app.todo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent;

import javax.persistence.Id;
import javax.persistence.Version;

/**
 * @author Vyacheslav Rusakov
 * @since 11.09.2025
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Persistent
public class Todo {
    @Id
    private String id;
    @Version
    private Long version;

    private String title;
    private Boolean completed;

    public Todo update(Todo patch) {

        if (patch.completed != null) {
            completed = patch.completed;
        }

        if (patch.title != null) {
            title = patch.title;
        }

        return this;
    }
}
