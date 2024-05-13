package edu.boisestate.murelbench.benchmarks;

import java.util.function.Supplier;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import edu.boisestate.murelbench.benchmarks.states.SyntheticConstraintThunks;
import edu.boisestate.murelbench.benchmarks.states.SyntheticOctagon;
import edu.boisestate.murelbench.domains.Constraint;
import edu.boisestate.murelbench.domains.OctagonDifferenceBoundedMatrix;

public class OctagonClosureBench {

    @Benchmark
    public void incrementalClosure(SyntheticOctagon o, SyntheticConstraintThunks c, Blackhole bh) {
        boolean feasible = c.thunks
            .stream()
            .<Supplier<Boolean>>map(t -> () -> o.matrix.putIncremental(t.s % o.N, t.t % o.N, t.c))
            .map(e -> e.get())
            .reduce((a, b) -> a && b)
            .orElse(false);
        assert !feasible || assertCanonical(o.matrix) : "Octagon was not canonical!";
        bh.consume(feasible);
    }

    @Benchmark
    public void chawdharyIncrementalClosure(SyntheticOctagon o, SyntheticConstraintThunks c, Blackhole bh) {
        boolean feasible = c.thunks.stream()
            .<Supplier<Boolean>>map(t -> () -> o.matrix.putIncrementalZ(t.s % o.N, t.t % o.N, t.c))
            .map(e -> e.get())
            .reduce((a, b) -> a && b)
            .orElse(false);
        assert !feasible || assertCanonical(o.matrix) : "Octagon was not canonical!";
        bh.consume(feasible);
    }

    @Benchmark
    public void fullClosure(SyntheticOctagon o, SyntheticConstraintThunks c, Blackhole bh) {
        boolean feasible = c.thunks.stream().map(t -> {
            o.matrix.putConstraint(t.s % o.N, t.t % o.N, t.c);
            return o.matrix.canonicalize();
        }).reduce((a, b) -> a && b)
            .orElse(false);
        assert !feasible || assertCanonical(o.matrix) : "Octagon was not canonical!";
        bh.consume(feasible);
    }

    public static boolean assertCanonical(OctagonDifferenceBoundedMatrix matrix) {
        if (matrix.isFeasible()) {
            for (int k = 0; k < matrix.size(); k++) {
                for (int i = 0; i < matrix.size(); i++) {
                    for (int j = 0; j < matrix.size(); j++) {
                        if ((assertTightClosure(matrix, i) &&
                              assertStrongClosure(matrix, i, j) &&
                              assertTransitiveClosure(matrix, i, j, k) &&
                              assertCoherent(matrix, i, j)) == false) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean assertTransitiveClosure(OctagonDifferenceBoundedMatrix matrix, int i, int j, int k) {
        Constraint zero = Constraint.of(0);
        Constraint ij = matrix.getConstraint(i, j);
        Constraint ik = matrix.getConstraint(i, k);
        Constraint kj = matrix.getConstraint(k, j);
        return ((i == j && Constraint.compare(ij, zero) == 0) ||
                ((Constraint.compare(ij, Constraint.add(ik, kj)) <= 0)));
    }

    public static boolean assertTightClosure(OctagonDifferenceBoundedMatrix matrix, int i) {
        Constraint iibar = matrix.getConstraint(i, i ^ 1);
        return (iibar.bound().map(b -> b % 2 == 0).orElse(true));
    }

    public static boolean assertStrongClosure(OctagonDifferenceBoundedMatrix matrix, int i, int j) {
        Constraint ij = matrix.getConstraint(i, j);
        Constraint iibar = matrix.getConstraint(i, i ^ 1);
        Constraint jbarj = matrix.getConstraint(j ^ 1 , j);
        return (Constraint.compare(ij, Constraint.add(iibar, jbarj)) <= 0);
    }

    public static boolean assertCoherent(OctagonDifferenceBoundedMatrix matrix, int i, int j) {
        Constraint ij = matrix.getConstraint(i, j);
        Constraint jbaribar = matrix.getConstraint(j ^ 1, i ^ 1);
        return ((i ^ j) == 1 || Constraint.compare(ij, jbaribar) == 0);
    }
}
