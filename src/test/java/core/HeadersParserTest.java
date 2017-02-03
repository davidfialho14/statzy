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
import static org.junit.Assert.assertThat;

public class HeadersParserTest {

    // UNIT under test
    private HeadersParser headersParser;

    @Rule
    public ExpectedException catcher = ExpectedException.none();

    private static Reader createFakeFileWithLines(String... lines) {
        // join all lines in the file with a new line
        String fileContent = Arrays.stream(lines)
                .collect(Collectors.joining("\n"));

        return new StringReader(fileContent);
    }

    @Test
    public void
    parse_FileWithOnlyHeadersRowWithDateAndTimeAndH1_HeadersDateAndTimeAndH1() throws Exception {
        Reader reader = createFakeFileWithLines("Date, Time, H1");
        headersParser = new HeadersParser(reader);

        assertThat(headersParser.parse(),
                is(Arrays.asList("Date", "Time", "H1")));
    }

    @Test
    public void
    parse_FileWithOnlySomeHeadersWithUnitTags_HeadersWithUnitTagIncludeUniTagInTheName() throws Exception {
        Reader reader = createFakeFileWithLines(
                "Date, Time, H1, H2",
                ", , m, s");
        headersParser = new HeadersParser(reader);

        assertThat(headersParser.parse(),
                is(Arrays.asList("Date", "Time", "H1 (m)", "H2 (s)")));
    }

    @Test
    public void
    parse_FileWithUnitTagsRowHas5ColumnsAndNamesRowHas4_ThrowsParseException() throws Exception {
        Reader reader = createFakeFileWithLines(
                "Date, Time, H1, H2",
                ", , m, s,");
        headersParser = new HeadersParser(reader);

        catcher.expect(ParseException.class);
        catcher.expectMessage("The row with the header names and the row with the unit tags do " +
                "not have the same number of columns: names row has 4 and unit" +
                " tags row has 5 columns.");
        headersParser.parse();
    }

    @Test
    public void
    parse_FileWithUnitTagsRowHas3ColumnsAndNamesRowHas4_ThrowsParseException() throws Exception {
        Reader reader = createFakeFileWithLines(
                "Date, Time, H1, H2",
                ", m, s");
        headersParser = new HeadersParser(reader);

        catcher.expect(ParseException.class);
        catcher.expectMessage("The row with the header names and the row with the unit tags do " +
                "not have the same number of columns: names row has 4 and unit" +
                " tags row has 3 columns.");
        headersParser.parse();
    }

    @Test
    public void
    parse_EmptyFile_ThrowsParseException() throws Exception {
        Reader reader = createFakeFileWithLines();
        headersParser = new HeadersParser(reader);

        catcher.expect(ParseException.class);
        catcher.expectMessage("Headers file is empty.");
        headersParser.parse();
    }

    @Test
    public void
    parse_HeadersFileWith3Rows_ThirdRowIsIgnored() throws Exception {
        Reader reader = createFakeFileWithLines(
                "Date, Time, H1, H2",
                ", , m, s",
                "something");
        headersParser = new HeadersParser(reader);

        assertThat(headersParser.parse(),
                is(Arrays.asList("Date", "Time", "H1 (m)", "H2 (s)")));
    }

}