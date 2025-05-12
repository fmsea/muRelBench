package dev.fmsea.murelbench.domains.providers;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.fmsea.murelbench.domains.Constraint;
import dev.fmsea.murelbench.utils.GlobalRandom;

public class ConstraintProvider {

    public static Constraint sample() {
        return sample(1).stream().findFirst().get();
    }

    public static Set<Constraint> sample(int N) {
        Random r = GlobalRandom.getRandom();
        return IntStream.range(0, N)
            .boxed()
            .map(_i -> Constraint.of(r.nextInt(Integer.MAX_VALUE)))
            .collect(Collectors.toSet());
    }
}
