package ru.vyarus.dropwizard.orient.health;

import com.codahale.metrics.health.HealthCheck;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.memory.OMemoryWatchDog;

/**
 * Checks embedded orient server state.
 * Looks for registered storage instances and available memory.
 *
 * @author Vyacheslav Rusakov
 * @since 05.10.2014
 */
public class OrientServerHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        final Orient instance = Orient.instance();
        final boolean noStorages = instance.getStorages().isEmpty();
        final OMemoryWatchDog memoryWatchDog = instance.getMemoryWatchDog();
        final boolean noMemory = memoryWatchDog == null
                || !memoryWatchDog.isMemoryAvailable();
        Result result;
        if (noStorages) {
            result = Result.unhealthy("No registered storages");
        } else if (noMemory) {
            result = Result.unhealthy(String.format("No memory available (used heap %s)",
                    memoryWatchDog == null ? "unknown" : memoryWatchDog.getUsedHeapMemory()));
        } else {
            result = Result.healthy("OK");
        }
        return result;
    }
}
