package dev.fmsea.murelbench.benchmarks.states;

import java.util.Set;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import dev.fmsea.murelbench.domains.ConstraintUpdateThunk;
import dev.fmsea.murelbench.domains.providers.ConstraintUpdateThunkProvider;

@State(Scope.Thread)
public class SyntheticConstraintThunks {
    @Param({"1"})
    int count;

    public Set<ConstraintUpdateThunk> thunks;

    @Setup
    public void prepare() {
        this.thunks = ConstraintUpdateThunkProvider.sample(count);
    }
}
