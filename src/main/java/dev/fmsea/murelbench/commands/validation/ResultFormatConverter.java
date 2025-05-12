package dev.fmsea.murelbench.commands.validation;

import org.openjdk.jmh.results.format.ResultFormatType;

import picocli.CommandLine.ITypeConverter;

public class ResultFormatConverter implements ITypeConverter<ResultFormatType> {
    public ResultFormatType convert(String value) throws Exception {
        if ("csv".equals(value.toLowerCase())) {
            return ResultFormatType.CSV;
        } else if ("json".equals(value.toLowerCase())) {
            return ResultFormatType.JSON;
        } else if ("scsv".equals(value.toLowerCase())) {
            return ResultFormatType.SCSV;
        } else if ("latex".equals(value.toLowerCase())) {
            return ResultFormatType.LATEX;
        } else if ("text".equals(value.toLowerCase())) {
            return ResultFormatType.TEXT;
        } else {
            return ResultFormatType.TEXT;
        }
    }
}
