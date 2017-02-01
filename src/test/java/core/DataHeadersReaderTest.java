package core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DataHeadersReaderTest {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Rule
    public ExpectedException catcher = ExpectedException.none();

    private static Reader fakeFile(String... lines) {
        // join all lines in the file with a new line
        String fileContent = Arrays.stream(lines)
                .collect(Collectors.joining("\n"));

        return new StringReader(fileContent);
    }

    private static List<String> data(String... headers) {
        return Arrays.asList(headers);
    }

    private static String date(String header) {
        return header;
    }

    private static String time(String header) {
        return header;
    }

    private void checkThatIs(Headers headers, List<String> dataHeaders, String dateHeader,
                                    String timeHeader) {

        collector.checkThat(headers.getDataHeaders(), is(dataHeaders));
        collector.checkThat(headers.getDateHeader(), is(dateHeader));
        collector.checkThat(headers.getTimeHeader(), is(timeHeader));
    }

    @Test
    public void
    read_FileWithHeaderNamesDateAndTimeAndH1_DateHeaderIsDate_TimeHeaderIsTime_DataHeadersAreH1() throws Exception {
        Reader reader = fakeFile(
                "Date, Time, H1"
        );
        int dateColumn = 0;
        int timeColumn = 1;
        Set<Integer> ignoredColumns = Collections.emptySet();

        try (HeadersReader headersReader = new HeadersReader(reader, dateColumn, timeColumn, ignoredColumns)) {
            Headers headers = headersReader.read();

            checkThatIs(headers, data("H1"), date("Date"), time("Time"));
        }
    }

    @Test
    public void read_FileWith2DataHeadersH1AndH2_DataHeadersAreH1AndH2() throws Exception {
        Reader reader = fakeFile(
                "Date, Time, H1, H2"
        );
        int dateColumn = 0;
        int timeColumn = 1;
        Set<Integer> ignoredColumns = Collections.emptySet();

        try (HeadersReader headersReader = new HeadersReader(reader, dateColumn, timeColumn, ignoredColumns)) {
            Headers headers = headersReader.read();

            checkThatIs(headers, data("H1", "H2"), date("Date"), time("Time"));
        }
    }

    @Test
    public void read_FileWith2DataHeadersIgnoreH2_DataHeadersAreOnlyH1() throws Exception {
        Reader reader = fakeFile(
                "Date, Time, H1, H2"
        );
        int dateColumn = 0;
        int timeColumn = 1;
        Set<Integer> ignoredColumns = Collections.singleton(3);

        try (HeadersReader headersReader = new HeadersReader(reader, dateColumn, timeColumn, ignoredColumns)) {
            Headers headers = headersReader.read();

            checkThatIs(headers, data("H1"), date("Date"), time("Time"));
        }
    }

    @Test
    public void read_HeadersWithUnitTags_HeadersH1AndH2IncludeUnitTagsMAndS() throws Exception {
        Reader reader = fakeFile(
                "Date, Time, H1, H2",
                ", , m, s"
        );
        int dateColumn = 0;
        int timeColumn = 1;
        Set<Integer> ignoredColumns = Collections.emptySet();

        try (HeadersReader headersReader = new HeadersReader(reader, dateColumn, timeColumn, ignoredColumns)) {
            Headers headers = headersReader.read();

            checkThatIs(headers, data("H1 (m)", "H2 (s)"), date("Date"), time("Time"));
        }
    }

    @Test
    public void read_NamesAndUnitTagsRecordHaveDifferentNumberOfColumns_ThrowsParseException() throws Exception {
        Reader reader = fakeFile(
                "Date, Time, H1, H2",
                ", , m, s,"
        );
        int dateColumn = 0;
        int timeColumn = 1;
        Set<Integer> ignoredColumns = Collections.emptySet();

        try (HeadersReader headersReader = new HeadersReader(reader, dateColumn, timeColumn, ignoredColumns)) {
            catcher.expect(ParseException.class);
            catcher.expectMessage("Headers file has 4 headers and 5 unit tag columns");
            headersReader.read();
        }
    }

    @Test
    public void read_EmptyFile_ThrowsParseException() throws Exception {
        Reader reader = fakeFile();
        int dateColumn = 0;
        int timeColumn = 1;
        Set<Integer> ignoredColumns = Collections.emptySet();

        try (HeadersReader headersReader = new HeadersReader(reader, dateColumn, timeColumn, ignoredColumns)) {
            catcher.expect(ParseException.class);
            catcher.expectMessage("Headers file is empty");
            headersReader.read();
        }
    }

    @Test
    public void read_MissingTimeHeader_ThrowsParseException() throws Exception {
        Reader reader = fakeFile(
                "Date"
        );
        int dateColumn = 0;
        int timeColumn = 1;
        Set<Integer> ignoredColumns = Collections.emptySet();

        try (HeadersReader headersReader = new HeadersReader(reader, dateColumn, timeColumn, ignoredColumns)) {
            catcher.expect(ParseException.class);
            catcher.expectMessage("Headers file was expected to have at least 2 header names, " +
                    "but got only 1");
            headersReader.read();
        }
    }

    @Test
    public void read_HeadersFileWith3Lines_IgnoresExtraLine() throws Exception {
        Reader reader = fakeFile(
                "Date, Time, H1, H2",
                ", , m, s",
                "extra line"
        );
        int dateColumn = 0;
        int timeColumn = 1;
        Set<Integer> ignoredColumns = Collections.emptySet();

        try (HeadersReader headersReader = new HeadersReader(reader, dateColumn, timeColumn, ignoredColumns)) {
            Headers headers = headersReader.read();
            
            checkThatIs(headers, data("H1 (m)", "H2 (s)"), date("Date"), time("Time"));
        }
    }

}