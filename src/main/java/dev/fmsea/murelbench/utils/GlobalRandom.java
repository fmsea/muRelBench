package dev.fmsea.murelbench.utils;

public class GlobalRandom {

    private static GlobalRandom instance;
    private final java.util.Random random;

    private GlobalRandom(long seed) {
        this.random = new java.util.Random(seed);
    }

    public static java.util.Random getRandom() {
        if (instance == null) {
            instance = new GlobalRandom(Properties.randomSeed);
        }
        return instance.random;
    }
}
