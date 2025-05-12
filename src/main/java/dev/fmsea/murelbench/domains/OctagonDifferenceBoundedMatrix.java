package dev.fmsea.murelbench.domains;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class OctagonDifferenceBoundedMatrix {

    protected final int N;
    protected Constraint[][] matrix;
    public OctagonDifferenceBoundedMatrix(int N, boolean top) {
        this.N = N;
        this.matrix = new Constraint[N][N];

        if (top) {
            iterateMatrix((i, j) -> {
                    this.matrix[i][j] = i == j ? Constraint.of(0) : Constraint.TOP();
                });
        } else {
            iterateMatrix((i, j) -> {
                    this.matrix[i][j] = Constraint.BOT();
                });
        }
    }

    public OctagonDifferenceBoundedMatrix(OctagonDifferenceBoundedMatrix copy) {
        this(copy.N, false);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.matrix[i][j] = copy.matrix[i][j].copy();
            }
        }
    }

    public int size() {
        return this.N;
    }

    public void copyTo(OctagonDifferenceBoundedMatrix destination) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                destination.matrix[i][j] = this.matrix[i][j].copy();
            }
        }
    }

    public void setConstraint(int i, int j, Constraint c) {
        if (i == j && c.bound().map(b -> b < 0).orElse(false)) {
            this.matrix[i][j] = Constraint.BOT();
        } else if (i == j && (!c.isBottom() || c.bound().map(b -> b > 0).orElse(false))) {
            this.matrix[i][j] = Constraint.of(0);
        } else {
            this.matrix[i][j] = c;
        }
    }

    public boolean putConstraint(int i, int j, Constraint constraint) {
        return this.putConstraint(i, j, constraint, this);
    }

    public boolean putConstraint(int i, int j, Constraint c, OctagonDifferenceBoundedMatrix in) {
        boolean added = false;
        if (Constraint.compare(c, in.matrix[i][j]) < 0) {
            this.setConstraint(i, j, c);
            added = true;
        }
        return added;
    }

    public boolean putIncremental(int i, int j, Constraint constraint) {
        return this.putIncremental(i, j, constraint, this);
    }

    public boolean putIncremental(int i, int j, Constraint constraint, OctagonDifferenceBoundedMatrix in) {
        return this.incrementalClosure(i, j, constraint);
    }

    public boolean putIncrementalZ(int i, int j, Constraint constraint) {
        return this.putIncrementalZ(i, j, constraint, this);
    }

    public boolean putIncrementalZ(int i, int j, Constraint constraint, OctagonDifferenceBoundedMatrix in) {
        if (putConstraint(i, j, constraint, in)) {
            return this.incrementalZClosure(i, j, constraint);
        } else {
            return this.isFeasible();
        }
    }

    public Constraint getConstraint(int i, int j) {
        return this.matrix[i][j];
    }

    public boolean isTop() {
        return reduceMatrixToBool((i, j) -> {
                if (i == j) {
                    return this.matrix[i][j].equals(Constraint.of(0));
                } else {
                    return this.matrix[i][j].isTop();
                }
            });
    }

    public boolean isFeasible() {
        boolean feasible = true;
        for (int i = 0; i < N; i++) {
            if (this.matrix[i][i].bound().map(b -> b < 0).orElse(false)) {
                feasible = false;
                break;
            }
        }
        feasible = feasible && !this.anyBottoms();
        return feasible;
    }

    public boolean isSubset(OctagonDifferenceBoundedMatrix other) {
        return this.reduceMatrixToBool((i, j) -> {
                return Constraint.compare(this.matrix[i][j],
                                          other.matrix[i][j]) <= 0;
            });
    }

    public boolean incrementalClosure(int si, int ti, Constraint d) {
        return this.incrementalClosure(List.of(ConstraintUpdateThunk.of(si, ti, d)));
    }

    public boolean incrementalClosure(Collection<ConstraintUpdateThunk> thunks) {
        return this.incrementalClosure(thunks, this);
    }

    public boolean incrementalClosure(Collection<ConstraintUpdateThunk> thunks, OctagonDifferenceBoundedMatrix in) {
        Set<Integer> worklist = new HashSet<>();
        thunks.stream().forEach(thunk -> {
                boolean added = this.putConstraint(thunk.s, thunk.t, thunk.c, in);
                if (added) {
                    worklist.add(thunk.t);
                }
            });

        if (worklist.isEmpty()) {
            return this.isFeasible();
        }

        for (int i = 0; i < N; i++) {
            for (ConstraintUpdateThunk thunk : thunks) {
                Constraint sum = Constraint.add(this.matrix[thunk.s][thunk.t], this.matrix[thunk.t][i]);
                if (this.putConstraint(thunk.s, i, sum, this)) {
                    worklist.add(i);
                }
            }
        }

        for (int i = 0; i < N; i++) {
            for (Integer c : worklist) {
                for (ConstraintUpdateThunk thunk : thunks) {
                    Constraint longPath = Constraint.add(this.matrix[i][thunk.s], this.matrix[thunk.s][c]);
                    if (Constraint.compare(this.matrix[i][c], longPath) > 0) {
                        this.setConstraint(i, c, longPath);
                    }
                }
            }
        }

        return this.tighten() && this.isZConsistent() && this.computeStrongClosure();
    }

    public boolean incrementalZClosure(int si, int ti, Constraint d) {
        return incrementalZClosure(ConstraintUpdateThunk.of(si, ti, d));
    }

    public boolean incrementalZClosure(ConstraintUpdateThunk triple) {
        Constraint two = Constraint.of(2);
        Constraint zero = Constraint.of(0);

        for (int i = 0; i < N; i++) {
            int ibar = i ^ 1;
            Constraint min = Constraint.min(Stream.<Constraint>of(
                this.matrix[i][ibar],
                Constraint.add(this.matrix[i][triple.s],
                    triple.c,
                    this.matrix[triple.t][ibar]),
                Constraint.add(this.matrix[i][triple.tbar],
                    triple.c,
                    this.matrix[triple.sbar][ibar]),
                Constraint.add(this.matrix[i][triple.tbar],
                    triple.c,
                    this.matrix[triple.sbar][triple.s],
                    triple.c,
                    this.matrix[triple.t][ibar]),
                Constraint.add(this.matrix[i][triple.s],
                    triple.c,
                    this.matrix[triple.t][triple.tbar],
                    triple.c,
                    this.matrix[triple.sbar][ibar])));
            this.matrix[i][ibar] = Constraint.multiply(two, Constraint.divide(min, two));
        }

        if (!isZConsistent()) {
            return false;
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (j != (i ^ 1)) {
                    int ibar = i ^ 1;
                    int jbar = j ^ 1;
                    Stream<Constraint> candidates = Stream.<Constraint>of(
                        this.matrix[i][j],
                        Constraint.add(this.matrix[i][triple.s],
                            triple.c,
                            this.matrix[triple.t][j]),
                        Constraint.add(this.matrix[i][triple.tbar],
                            triple.c,
                            this.matrix[triple.sbar][j]),
                        Constraint.add(this.matrix[i][triple.tbar],
                            triple.c,
                            this.matrix[triple.sbar][triple.s],
                            triple.c,
                            this.matrix[triple.t][j]),
                        Constraint.add(this.matrix[i][triple.s],
                            triple.c,
                            this.matrix[triple.t][triple.tbar],
                            triple.c,
                            this.matrix[triple.sbar][j]),
                        Constraint.divide(Constraint.add(this.matrix[i][ibar],
                            this.matrix[jbar][j]),
                            two));
                    Constraint min = Constraint.min(candidates);
                    this.matrix[i][j] = min;
                }
            }
            if (Constraint.compare(this.matrix[i][i], zero) < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean computeClosure(boolean recompute) {
        // skip closure if matrix contains bottoms
        if (this.anyBottoms()) {
            return false;
        }

        if (recompute) {
            // ensure diagonal is zeroed.
            for (int i = 0; i < N; i++) {
                this.matrix[i][i] = Constraint.of(0);
            }

            for (int k = 0; k < N; k++) {
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        Constraint[] candidates = new Constraint[] {
                            this.matrix[i][j],
                            Constraint.add(this.matrix[i][k],
                                           this.matrix[k][j]),
                        };
                        Constraint min = Constraint.min(Stream.of(candidates));
                        this.matrix[i][j] = min;
                        if (min.equals(Constraint.BOT())) {
                            bottomOut();
                            return false;
                        }
                    }
                }
            }
        }

        boolean feasible = true;
        for (int i = 0; i < N; i++) {
            if (this.matrix[i][i].isBottom() || this.matrix[i][i].bound().orElse(0) < 0) {
                feasible = false;
                bottomOut();
                break;
            }
        }
        return feasible;
    }

    public boolean computeClosure() {
        return computeClosure(false);
    }

    public boolean computeStrongClosure() {
        // Assumes closure

        // skip strong closure if infeasible
        if (!isFeasible()) {
            return false;
        }

        Constraint two = Constraint.of(2);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Constraint t = Constraint.divide(Constraint.add(this.matrix[i][i ^ 1],
                                                                this.matrix[j ^ 1][j]),
                                                 two);
                this.matrix[i][j] = Constraint.min(this.matrix[i][j], t);
            }
        }

        return this.isFeasible();
    }

    public boolean canonicalize(boolean recompute) {
        return (computeClosure(recompute) &&
                tighten() &&
                isZConsistent() &&
                computeStrongClosure());
    }

    public boolean canonicalize() {
        return canonicalize(false);
    }

    public boolean tighten() {
        Constraint two = Constraint.of(2);
        for (int i = 0; i < N; i++) {
            Constraint tight = Constraint.multiply(Constraint.divide(this.matrix[i][i ^ 1], two), two);
            this.matrix[i][i ^ 1] = tight;
        }
        return true;
    }

    public boolean isZConsistent() {
        boolean consistent = true;
        if (!this.isFeasible()) {
            consistent = false;
        }
        for (int i = 0; i < N && consistent; i++) {
            if (Constraint.compare(Constraint.add(this.matrix[i][i ^ 1],
                                                  this.matrix[i ^ 1][i]),
                                   Constraint.of(0)) < 0) {
                consistent = false;
                break;
            }
        }
        return consistent;
    }

    @Override public boolean equals(Object o) {
        boolean equal = false;
        if (o != null && o instanceof OctagonDifferenceBoundedMatrix) {
            equal = this.equals((OctagonDifferenceBoundedMatrix) o);
        }
        return equal;
    }

    public boolean equals(OctagonDifferenceBoundedMatrix other) {
        if (this.N != other.N) {
            return false;
        } else {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (!this.matrix[i][j].equals(other.matrix[i][j])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + N;
        result = prime * result + this.matrix.hashCode();
        return result;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(Arrays.deepToString(this.matrix).replace("],", "],\n"));
        return sb.toString();
    }

    private void bottomOut() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.matrix[i][j] = Constraint.BOT();
            }
        }
    }

    private boolean anyBottoms() {
        final Constraint bot = Constraint.BOT();
        return reduceMatrix((i, j) -> this.matrix[i][j].equals(bot),
                            (a, b) -> a || b,
                            false);
    }

    private <R> R reduceMatrix(BiFunction<Integer, Integer, R> fmap,
                               BinaryOperator<R> acc,
                               R initial) {
        R ret = initial;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                ret = acc.apply(ret, fmap.apply(i, j));
            }
        }
        return ret;
    }

    private void iterateMatrix(BiConsumer<Integer, Integer> op) {
        reduceMatrixToBool((i, j) -> {
                op.accept(i, j);
                return true;
            });
    }

    private boolean reduceMatrixToBool(BiFunction<Integer, Integer, Boolean> reducer) {
        return reduceMatrix(reducer, (a, b) -> a && b, true);
    }
}
