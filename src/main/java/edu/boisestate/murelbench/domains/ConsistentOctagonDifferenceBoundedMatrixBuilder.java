package edu.boisestate.murelbench.domains;

public class ConsistentOctagonDifferenceBoundedMatrixBuilder extends OctagonDifferenceBoundedMatrixBuilder {

    public ConsistentOctagonDifferenceBoundedMatrixBuilder(int N, boolean top) {
        super(N, top);
    }

    /** Add a constraint to the matrix, return whether the system remains consistent
     *
     * If the system becomes inconsistent, reject the addition
    */
    public boolean maybeSetConstraint(int i, int j, Constraint c) {
        var copy = new OctagonDifferenceBoundedMatrix(this.matrix);
        copy.setConstraint(i, j, c);
        copy.setConstraint(j ^ 1, i ^ 1, c.copy());
        if (copy.computeClosure()) {
            this.matrix.setConstraint(i, j, c);
            this.matrix.setConstraint(j ^ 1, i ^ 1, c.copy());
            return true;
        } else {
            return false;
        }
    }
}
