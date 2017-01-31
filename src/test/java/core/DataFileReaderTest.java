package core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static core.DataRecordFactory.factoryExpectingItemCountPerRecord;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataFileReaderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DataFileReader dataFileReader;
    private final DataRecordFactory dataRecordFactory = factoryExpectingItemCountPerRecord(4)
            .withDateInColumn(0)
            .withTimeInColumn(1)
            .build();

    private static Reader fakeFile(String... lines) {
        // join all lines in the file with a new line
        String fileContent = Arrays.stream(lines)
                .collect(Collectors.joining("\n"));

        return new StringReader(fileContent);
    }

    @Test
    public void read_FileWith2DataLines_Reads2DataRecords() throws Exception {
        Reader file = fakeFile(
                "09/08/2016, 11:22:00,   176,   186",
                "09/08/2016, 11:22:30,   123,   144");
        dataFileReader = new DataFileReader(file, dataRecordFactory);

        Timestamp expectedFirstRecordsTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 0);
        Timestamp expectedSecondRecordsTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 30);

        assertThat(dataFileReader.read(),
                is(new DataRecord(expectedFirstRecordsTimestamp, Arrays.asList(176.0, 186.0))));
        assertThat(dataFileReader.read(),
                is(new DataRecord(expectedSecondRecordsTimestamp, Arrays.asList(123.0, 144.0))));
    }

    @Test
    public void read_EmptyFile_ReturnsNullOnFirstCall() throws Exception {
        Reader emptyFile = fakeFile();
        dataFileReader = new DataFileReader(emptyFile, dataRecordFactory);

        assertThat(dataFileReader.read(),
                is(nullValue()));
    }

    @Test
    public void read_FileWithASingleEmptyFile_ReturnsNullOnFirstCall() throws Exception {
        Reader file = fakeFile("");
        dataFileReader = new DataFileReader(file, dataRecordFactory);

        assertThat(dataFileReader.read(),
                is(nullValue()));
    }

    @Test
    public void read_FileWith2DataLinesWithAnEmptyLineBetween_Reads2DataRecordsAndIgnoresEmptyLine() throws Exception {
        Reader file = fakeFile(
                "09/08/2016, 11:22:00,   176,   186",
                "",
                "09/08/2016, 11:22:30,   123,   144");
        dataFileReader = new DataFileReader(file, dataRecordFactory);

        Timestamp expectedFirstRecordsTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 0);
        Timestamp expectedSecondRecordsTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 30);

        assertThat(dataFileReader.read(),
                is(new DataRecord(expectedFirstRecordsTimestamp, Arrays.asList(176.0, 186.0))));
        assertThat(dataFileReader.read(),
                is(new DataRecord(expectedSecondRecordsTimestamp, Arrays.asList(123.0, 144.0))));
    }

    @Test
    public void
    read_FileWith2DataLinesAndTimestampInSecondLinePredatesFirstLine_ThrowsParseException() throws Exception {
        Reader file = fakeFile(
                "09/08/2016, 11:22:30,   176,   186",
                "",
                "09/08/2016, 11:22:00,   123,   144");
        dataFileReader = new DataFileReader(file, dataRecordFactory);
        dataFileReader.read();

        thrown.expect(ParseException.class);
        thrown.expectMessage("Timestamp in line 3 predates the timestamp in the previous record");

        dataFileReader.read();
    }

}