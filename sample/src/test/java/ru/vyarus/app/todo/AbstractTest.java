package ru.vyarus.app.todo;

/**
 * @author Vyacheslav Rusakov
 * @since 20.10.2025
 */
public abstract class AbstractTest {

    protected void WHEN(String message) {
        System.out.println("\n!!! [WHEN] " + message + "\n");
    }
}
