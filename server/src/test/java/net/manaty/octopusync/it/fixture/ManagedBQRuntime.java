package net.manaty.octopusync.it.fixture;

import io.bootique.BQRuntime;
import io.bootique.command.CommandOutcome;

public interface ManagedBQRuntime {

    CommandOutcome run();

    BQRuntime getRuntime();
}
