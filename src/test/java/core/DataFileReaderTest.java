package core;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataFileReaderTest {

    private DataFileReader dataFileReader;
    private final DataRecordFactory dataRecordFactory = null;

    private static Reader fakeFile(String... lines) {
        // join all lines in the file with a new line
        String fileContent = Arrays.stream(lines)
                .collect(Collectors.joining("\n"));

        return new StringReader(fileContent);
    }

    @Test
    public void read_FileWith2DataLines_Reads2DataRecords() throws Exception {
        Reader file = fakeFile(
                "09/08/2016, 00:00:00,   176,   186",
                "09/08/2016, 00:00:01,   123,   144"
        );

        dataFileReader = new DataFileReader(file, dataRecordFactory);

        assertThat(dataFileReader.read(),
                is(nullValue()));
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

}