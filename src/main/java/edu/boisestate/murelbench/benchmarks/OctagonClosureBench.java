package edu.boisestate.murelbench.benchmarks;

import java.util.function.Supplier;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import edu.boisestate.murelbench.benchmarks.states.SyntheticConstraintThunks;
import edu.boisestate.murelbench.benchmarks.states.SyntheticOctagon;

public class OctagonClosureBench {

    @Benchmark
    public void incrementalClosure(SyntheticOctagon o, SyntheticConstraintThunks c, Blackhole bh) {
        bh.consume(
            c.thunks
                .stream()
                .<Supplier<Boolean>>map(t -> () -> o.matrix.putIncremental(t.s % o.N, t.t % o.N, t.c))
                .map(e -> e.get())
                .reduce((a, b) -> a && b));
    }

    @Benchmark
    public void chawdharyIncrementalClosure(SyntheticOctagon o, SyntheticConstraintThunks c, Blackhole bh) {
        bh.consume(
            c.thunks.stream()
                .<Supplier<Boolean>>map(t -> () -> o.matrix.putIncrementalZ(t.s % o.N, t.t % o.N, t.c))
                .map(e -> e.get())
                .reduce((a, b) -> a && b));
    }

    @Benchmark
    public void fullClosure(SyntheticOctagon o, SyntheticConstraintThunks c, Blackhole bh) {
        c.thunks.stream().forEach(t -> {
            o.matrix.putConstraint(t.s % o.N, t.t % o.N, t.c);
            bh.consume(o.matrix.canonicalize());
        });
    }
}
