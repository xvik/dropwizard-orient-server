package ru.vyarus.app.todo.repository;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.google.inject.persist.Transactional;
import ru.vyarus.app.todo.model.Todo;
import ru.vyarus.guice.persist.orient.repository.command.query.Query;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.ObjectCrud;

/**
 * @author Vyacheslav Rusakov
 * @since 22.11.2025
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface TodoRepository extends ObjectCrud<Todo> {

    @Query("delete from Todo")
    void deleteAll();

}
