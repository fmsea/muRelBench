package dev.fmsea.murelbench.domains.providers;

import java.util.Random;

import dev.fmsea.murelbench.domains.Constraint;
import dev.fmsea.murelbench.domains.OctagonDifferenceBoundedMatrix;
import dev.fmsea.murelbench.domains.ConsistentOctagonDifferenceBoundedMatrixBuilder;
import dev.fmsea.murelbench.utils.GlobalRandom;

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
        ConsistentOctagonDifferenceBoundedMatrixBuilder mb = new ConsistentOctagonDifferenceBoundedMatrixBuilder(N, true);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)  {
                if (i == j) {
                    mb.setConstraint(i, j, Constraint.of(0));
                } else if (r.nextDouble() < density) {
                    Constraint c = ConstraintProvider.sample();
                    while (!mb.maybeSetConstraint(i, j, c)) {
                        c = ConstraintProvider.sample();
                    }
                }
            }
        }
        return mb.build();
    }
}
