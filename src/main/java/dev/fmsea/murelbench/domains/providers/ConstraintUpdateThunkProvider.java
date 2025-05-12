package dev.fmsea.murelbench.domains.providers;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dev.fmsea.murelbench.domains.Constraint;
import dev.fmsea.murelbench.domains.ConstraintUpdateThunk;
import dev.fmsea.murelbench.utils.GlobalRandom;

public class ConstraintUpdateThunkProvider {

    public static ConstraintUpdateThunk sample() {
        return sample(1).stream().findFirst().get();
    }

    public static Set<ConstraintUpdateThunk> sample(int N) {
        Random r = GlobalRandom.getRandom();
        return IntStream.range(0, N)
            .boxed()
            .flatMap(k -> {
            int s = r.nextInt(200);
            int t = r.nextInt(200);
            int c = r.nextInt();
            return Stream.of(
                ConstraintUpdateThunk.of(s, t, Constraint.of(c)),
                ConstraintUpdateThunk.of(t ^ 1, s ^ 1, Constraint.of(c)));
        }).collect(Collectors.toSet());
    }
}
