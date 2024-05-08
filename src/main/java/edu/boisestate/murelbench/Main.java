package edu.boisestate.murelbench;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import edu.boisestate.murelbench.commands.validation.ModeConverter;
import edu.boisestate.murelbench.commands.validation.ResultFormatConverter;
import edu.boisestate.murelbench.commands.validation.TimeUnitConverter;
import edu.boisestate.murelbench.utils.Properties;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    mixinStandardHelpOptions = true,
    description = "Execute Benchmarks")
public class Main implements Callable<Integer> {

    static class ResultOptions {
        @Option(names = {"--result-format"},
            description = "Format for results, when printing to a file.",
            defaultValue = "",
            converter = ResultFormatConverter.class)
        ResultFormatType formatType;
        @Option(names = {"--result"},
            description = "Filename for results",
            defaultValue = "")
        String filename;
    }

    @ArgGroup(exclusive = false)
    ResultOptions resultOptions;

    @Option(names = {"--seed"},
        description = "Seed value used for random number generation")
    protected long seed;

    @Option(names = {"--mode"},
        description = "Measurement Mode for benchmarks, all, single-shot, sample, and average.",
        defaultValue = "average",
        converter = ModeConverter.class)
    protected Mode measurementMode;

    @Option(names = {"--time-unit"},
        description = "Time Unit used to report benchmark results",
        defaultValue = "seconds",
        converter = TimeUnitConverter.class)
    protected TimeUnit timeUnit;

    @Option(names = {"--warmup-time"},
        description = "Benchmark warmup time in milliseconds",
        defaultValue = "1000")
    protected long warmupTime;

    @Option(names = {"--warmup-iterations"},
        description = "Number of iterations to use to warmup the JVM",
        defaultValue = "3")
    protected int warmupIterations;

    @Option(names = {"--measurement-time"},
        description = "Duration in milliseconds between measurements",
        defaultValue = "1000")
    protected long measurementTime;

    @Option(names = {"--measurement-iterations"},
        description = "Number of times to measure performance.  This multiplies with `--forks`.",
        defaultValue = "5")
    protected int measurementIterations;

    @Option(names = {"--threads"},
        description = "Number of JVM threads for each benchmark",
        defaultValue = "1")
    protected int threads;

    @Option(names = {"--forks"},
        description = "Number of experimental forks for each benchmark.  This multiplies with `--measurement-iterations`.",
        defaultValue = "1")
    protected int forks;

    @Option(names = {"--fail-on-error"},
        description = "Should benchmarks fail on errors",
        negatable = true,
        defaultValue = "true")
    protected boolean failOnError;

    @Option(names = {"--gc"},
        description = "Should the JVM perform Garbage Collection during benchmark execution.",
        negatable = true,
        defaultValue = "true")
    protected boolean doGC;

    @Option(names = {"--jvm-args"},
        description = "JVM Arguments to pass to the forked process for benchmarks",
        split = ",")
    protected String[] jvmArgs;

    @Option(names = {"--include"},
        description = "Pattern used to include benchmarks",
        defaultValue = "edu.boisestate.murelbench.benchmarks.*Bench")
    protected String benchmarkIncludes;

    @Option(names = {"--exclude"},
        description = "Pattern used to exclude some benchmarks")
    protected String benchmarkExcludes;

    @Parameters(description = "Extra arguments to be passed directly to JMH, use `--`")
    protected String[] jmhArguments;

    public Integer call() throws Exception {

        if (seed != 0) {
            Properties.randomSeed = seed;
        }
        System.err.println(String.format("Using %s for seed value", Properties.randomSeed));

        ChainedOptionsBuilder builder = new OptionsBuilder();

        if (jmhArguments != null) {
            builder.parent(new CommandLineOptions(jmhArguments));
        }

        builder.include(benchmarkIncludes)
            .mode(measurementMode)
            .timeUnit(timeUnit)
            .warmupTime(TimeValue.milliseconds(warmupTime))
            .warmupIterations(warmupIterations)
            .measurementTime(TimeValue.milliseconds(measurementTime))
            .measurementIterations(measurementIterations)
            .threads(threads)
            .forks(forks)
            .shouldFailOnError(failOnError)
            .shouldDoGC(doGC);

        if (!(benchmarkExcludes == null || benchmarkExcludes.isEmpty())) {

            builder.exclude(benchmarkExcludes);
        }

        if (jvmArgs != null) {
            builder.jvmArgsAppend(jvmArgs);
        }
        if (resultOptions != null) {
            builder.result(resultOptions.filename)
                .resultFormat(resultOptions.formatType);
        }
        Options opt = builder.build();

        try {
            new Runner(opt).run();
            return 0;
        } catch (Exception ex) {
            System.err.println(ex);
            return -1;
        }
    }

    public static void main(String[] args) {
        Integer exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
