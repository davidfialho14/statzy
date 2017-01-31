package core;

import org.junit.Test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DataFileWriterTest {

    private final TimestampFormatter formatter = TimestampFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

    private static final String END_LINE = "\r\n";

    private static List<Double> means(Double... means) {
        return Arrays.asList(means);
    }

    private static List<Double> stdevs(Double... stdevs) {
        return Arrays.asList(stdevs);
    }


    @Test
    public void
    write_RecordWithASingleItemValue_TimestampIn1stColAndCountIn2ndColAndAvgIn3rdAndStdevIn4thCol() throws Exception {
        StringWriter stringWriter = new StringWriter();

        try (DataFileWriter writer = new DataFileWriter(stringWriter, formatter)) {
            writer.write(Timestamp.of(2016, 11, 22, 1, 2, 3), 3, means(1.0), stdevs(2.0));
        }

        assertThat(stringWriter.toString(), is("2016/11/22 01:02:03,3,1.0,2.0" + END_LINE));
    }

    @Test
    public void
    write_RecordWithTwoItemValues_TimestampIn1stColAndCountIn2ndColAndAvgAndStdevForWachItem() throws Exception {
        StringWriter stringWriter = new StringWriter();

        try (DataFileWriter writer = new DataFileWriter(stringWriter, formatter)) {
            writer.write(Timestamp.of(2016, 11, 22, 1, 2, 3), 3, means(1.0, 2.0), stdevs(3.0, 4.0));
        }

        assertThat(stringWriter.toString(), is("2016/11/22 01:02:03,3,1.0,3.0,2.0,4.0" + END_LINE));
    }



}