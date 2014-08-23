package ru.vyarus.dropwizard.orient

import ru.vyarus.dropwizard.orient.support.TestApplication;
import spock.lang.Specification

/**
 * Base class for tests.
 *
 * @author Vyacheslav Rusakov
 * @since 16.07.2014
 */
abstract class AbstractTest extends Specification {

    def command(String attrs) {
        new TestApplication().run(attrs.split(' '))
    }
}
