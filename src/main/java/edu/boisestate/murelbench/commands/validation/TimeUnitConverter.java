package edu.boisestate.murelbench.commands.validation;

import java.util.concurrent.TimeUnit;

import picocli.CommandLine.ITypeConverter;

public class TimeUnitConverter implements ITypeConverter<TimeUnit> {
    public TimeUnit convert(String value) throws Exception {
        if ("s".equals(value.toLowerCase()) || "seconds".equals(value.toLowerCase())) {
            return TimeUnit.SECONDS;
        } else if ("ms".equals(value.toLowerCase()) || "milliseconds".equals(value.toLowerCase())) {
            return TimeUnit.MILLISECONDS;
        } else if ("microseconds".equals(value.toLowerCase())) {
            return TimeUnit.MICROSECONDS;
        } else if ("ns".equals(value.toLowerCase()) || "nanoseconds".equals(value.toLowerCase())) {
            return TimeUnit.NANOSECONDS;
        } else {
            return TimeUnit.SECONDS;
        }
    }
}
