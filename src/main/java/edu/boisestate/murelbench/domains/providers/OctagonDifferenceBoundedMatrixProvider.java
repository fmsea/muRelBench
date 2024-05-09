package edu.boisestate.murelbench.domains.providers;

import java.util.Random;

import edu.boisestate.murelbench.domains.Constraint;
import edu.boisestate.murelbench.domains.OctagonDifferenceBoundedMatrix;
import edu.boisestate.murelbench.domains.OctagonDifferenceBoundedMatrixBuilder;
import edu.boisestate.murelbench.utils.GlobalRandom;

public class OctagonDifferenceBoundedMatrixProvider{

    public static OctagonDifferenceBoundedMatrix sample() {
        Random r = GlobalRandom.getRandom();
        int N = 2 * (r.nextInt(8) + 1);
        return OctagonDifferenceBoundedMatrixProvider.sample(N);
    }

    public static OctagonDifferenceBoundedMatrix sample(int N) {
        return sample(N, 0.5);
    }

    public static OctagonDifferenceBoundedMatrix sample(int N, double density) {
        Random r = GlobalRandom.getRandom();
        OctagonDifferenceBoundedMatrixBuilder mb = new OctagonDifferenceBoundedMatrixBuilder(N, true);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)  {
                if (i == j) {
                    mb.setConstraint(i, j, Constraint.of(0));
                } else if (r.nextDouble() < density) {
                    Constraint c = ConstraintProvider.sample();
                    mb.setConstraint(i, j, c);
                    mb.setConstraint(j ^ 1, i ^ 1, c.copy());
                }
            }
        }
        return mb.build();
    }
}
