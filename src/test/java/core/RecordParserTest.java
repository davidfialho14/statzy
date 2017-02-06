package core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordParserTest {

    private RecordParser parser;    // UNIT under test

    @Rule
    public ExpectedException catcher = ExpectedException.none();

    private static Reader createFakeFileWithLines(String... lines) {
        // join all lines in the file with a new line
        String fileContent = Arrays.stream(lines)
                .collect(Collectors.joining("\n"));

        return new StringReader(fileContent);
    }

    @Test
    public void parseRecord_FromFileWith1RowWith4Columns_RecordWith4ValuesAndValuesAreTrimmed() throws Exception {
        Reader readerForFakeFile = createFakeFileWithLines(
                "09/08/2016, 11:22:00,   176,   186"
        );

        parser = new RecordParser(readerForFakeFile);

        assertThat(parser.parseRecord(),
                is(Record.from(Arrays.asList("09/08/2016", "11:22:00", "176", "186"), 1)));
    }

    @Test
    public void parseRecord_FromFileWith2RowsWith4Columns_2RecordsWith4Values() throws Exception {
        Reader readerForFakeFile = createFakeFileWithLines(
                "09/08/2016, 11:22:00, 176, 186",
                "09/08/2016, 21:12:00, 16, 12"
        );

        parser = new RecordParser(readerForFakeFile);

        assertThat(parser.parseRecord(),
                is(Record.from(Arrays.asList("09/08/2016", "11:22:00", "176", "186"), 1)));
        assertThat(parser.parseRecord(),
                is(Record.from(Arrays.asList("09/08/2016", "21:12:00", "16", "12"), 2)));
    }

    @Test
    public void
    parseRecord_FromFileWithEmptyRowFollowedByARowWith4Columns_IgnoresEmptyRowAndParsesRecordWith4Values() throws Exception {
        Reader readerForFakeFile = createFakeFileWithLines(
                "",
                "09/08/2016, 21:12:00, 16, 12"
        );

        parser = new RecordParser(readerForFakeFile);

        assertThat(parser.parseRecord(),
                is(Record.from(Arrays.asList("09/08/2016", "21:12:00", "16", "12"), 2)));
    }

    @Test
    public void
    parseRecord_FromFileWithRowWith4ColumnsAndRowWith3Columns_ThrowsParseExceptionOnSecondRecord()
            throws Exception {
        Reader readerForFakeFile = createFakeFileWithLines(
                "09/08/2016, 21:12:00, 16, 12",
                "09/08/2016, 21:34:00, 16"
        );

        parser = new RecordParser(readerForFakeFile);
        parser.parseRecord();

        catcher.expect(ParseException.class);
        catcher.expectMessage("Row has 3 columns, but 4 were expected.");
        parser.parseRecord();
    }

    @Test
    public void
    parseRecord_FromEmptyFile_ReturnsNull() throws Exception {
        Reader readerForFakeFile = createFakeFileWithLines();

        parser = new RecordParser(readerForFakeFile);

        assertThat(parser.parseRecord(), is(nullValue()));
    }

    @Test
    public void
    parseRecord_FromFileWithEmptyLine_ReturnsNull() throws Exception {
        Reader readerForFakeFile = createFakeFileWithLines("");

        parser = new RecordParser(readerForFakeFile);

        assertThat(parser.parseRecord(), is(nullValue()));
    }

}