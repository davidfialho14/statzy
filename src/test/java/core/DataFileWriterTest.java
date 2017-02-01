package core;

import org.junit.Test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DataFileWriterTest {

    private static final String datePattern = "uuuu/MM/dd";
    private static final String timePattern = "HH:mm:ss";
    private static final String END_LINE = "\r\n";

    private static List<Double> means(Double... means) {
        return Arrays.asList(means);
    }

    private static List<Double> stdevs(Double... stdevs) {
        return Arrays.asList(stdevs);
    }

    @Test
    public void
    write_ValidRecordWith1DataSetWithDateAndTimeInDifferentColumns_DateAndTimeInFirst2Columns() throws Exception {
        StringWriter output = new StringWriter();

        try (
                DataFileWriter writer = DataFileWriter.outputTo(output)
                        .withDatePattern(datePattern)
                        .withTimePattern(timePattern)
                        .inSameColumn(false)
                        .build()
        ) {
            writer.write(Timestamp.of(2016, 11, 22, 1, 2, 3), 3, means(1.0), stdevs(2.0));
        }

        assertThat(output.toString(), is("2016/11/22,01:02:03,3,1.0,2.0" + END_LINE));
    }

    @Test
    public void
    write_ValidRecordWith2DataSetsWithDateAndTimeInDifferentColumns_DateAndTimeInFirst2Columns()
            throws Exception {
        StringWriter output = new StringWriter();

        try (
                DataFileWriter writer = DataFileWriter.outputTo(output)
                        .withDatePattern(datePattern)
                        .withTimePattern(timePattern)
                        .inSameColumn(false)
                        .build()
        ) {
            writer.write(Timestamp.of(2016, 11, 22, 1, 2, 3), 3, means(1.0, 2.0), stdevs(3.0, 4.0));
        }

        assertThat(output.toString(), is("2016/11/22,01:02:03,3,1.0,3.0,2.0,4.0" + END_LINE));
    }

    @Test
    public void
    write_DateAndTimeInSameColumnDelimitedByTab_DateAndTimeInFirstColumnDelimitedByTab()
            throws Exception {
        StringWriter output = new StringWriter();

        try (
                DataFileWriter writer = DataFileWriter.outputTo(output)
                        .withDatePattern(datePattern)
                        .withTimePattern(timePattern)
                        .inSameColumn(true)
                        .delimitedBy(Delimiter.TAB)
                        .build()
        ) {
            writer.write(Timestamp.of(2016, 11, 22, 1, 2, 3), 3, means(1.0), stdevs(2.0));
        }

        assertThat(output.toString(), is("2016/11/22\t01:02:03,3,1.0,2.0" + END_LINE));
    }

    @Test
    public void
    write_DateAndTimeInSameColumnAndCommaAsDelimiter_DateAndTimeBetweenQuotationMarksAndDelimitedByComma() throws Exception {
        StringWriter output = new StringWriter();

        try (
                DataFileWriter writer = DataFileWriter.outputTo(output)
                        .withDatePattern(datePattern)
                        .withTimePattern(timePattern)
                        .inSameColumn(true)
                        .delimitedBy(Delimiter.COMMA)
                        .build()
        ) {
            writer.write(Timestamp.of(2016, 11, 22, 1, 2, 3), 3, means(1.0), stdevs(2.0));
        }

        assertThat(output.toString(), is("\"2016/11/22,01:02:03\",3,1.0,2.0" + END_LINE));
    }

    @Test
    public void
    write_TimeBeforeDateInDifferentColumns_TimeInFirstColumnAndDateInSecondColumn() throws Exception {
        StringWriter output = new StringWriter();

        try (
                DataFileWriter writer = DataFileWriter.outputTo(output)
                        .withDatePattern(datePattern)
                        .withTimePattern(timePattern)
                        .inSameColumn(false)
                        .withTimeBeforeDate(true)
                        .build()
        ) {
            writer.write(Timestamp.of(2016, 11, 22, 1, 2, 3), 3, means(1.0), stdevs(2.0));
        }

        assertThat(output.toString(), is("01:02:03,2016/11/22,3,1.0,2.0" + END_LINE));
    }

}