package edu.boisestate.murelbench.commands.validation;

import org.openjdk.jmh.annotations.Mode;
import picocli.CommandLine.ITypeConverter;

public class ModeConverter implements ITypeConverter<Mode> {
    public Mode convert(String value) throws Exception {
        if ("throughput".equals(value.toLowerCase())) {
            return Mode.Throughput;
        } else if ("average".equals(value.toLowerCase())) {
            return Mode.AverageTime;
        } else if ("sample".equals(value.toLowerCase())) {
            return Mode.SampleTime;
        } else if ("single-shot".equals(value.toLowerCase())) {
            return Mode.SingleShotTime;
        } else if ("all".equals(value.toLowerCase())) {
            return Mode.All;
        } else {
            return Mode.AverageTime;
        }
    }
}
