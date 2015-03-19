package ru.vyarus.dropwizard.orient.health;

import com.codahale.metrics.health.HealthCheck;
import com.orientechnologies.orient.core.Orient;

/**
 * Checks embedded orient server state.
 * Looks for registered storage instances and server active status.
 *
 * @author Vyacheslav Rusakov
 * @since 05.10.2014
 */
public class OrientServerHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        final Orient instance = Orient.instance();
        final boolean noStorages = instance.getStorages().isEmpty();
        Result result;
        if (!instance.isActive()) {
            result = Result.unhealthy("Database not started");
        } else if (noStorages) {
            result = Result.unhealthy("No registered storages");
        } else  {
            result = Result.healthy("OK");
        }
        return result;
    }
}
