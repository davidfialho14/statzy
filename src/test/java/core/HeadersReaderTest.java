package core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeadersReaderTest {

    // UNIT under test
    private HeadersReader headersReader;

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

    @SafeVarargs
    private static HeadersParser fakeParser(String... headers) throws ParseException {

        HeadersParser parserStub = mock(HeadersParser.class);
        when(parserStub.parse()).thenReturn(Arrays.asList(headers));

        return parserStub;
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

    private void checkThatIs(Headers headers, List<String> dataHeaders, String dateHeader, String timeHeader) {

        collector.checkThat(headers.getDataHeaders(), is(dataHeaders));
        collector.checkThat(headers.getDateHeader(), is(dateHeader));
        collector.checkThat(headers.getTimeHeader(), is(timeHeader));
    }

    @Test
    public void
    read_WithDateInCol0AndTimeInCol1AndTwoDataColumnsNotIgnored_CorrectDateAndTimeHeadersAnd2DataHeaders() throws Exception {
        HeadersParser fakeParser = fakeParser(
                "Date", "Time", "H1", "H2"
        );

        Set<Integer> ignoredColumns = Collections.emptySet();
        headersReader = new HeadersReader(fakeParser, 0, 1, ignoredColumns);

        checkThatIs(headersReader.read(), data("H1", "H2"), date("Date"), time("Time"));
    }

    @Test
    public void read_FileWith2DataHeadersAndHeaderH2IsIgnore_DataHeadersIncludesOnlyH1() throws Exception {
        HeadersParser fakeParser = fakeParser(
                "Date", "Time", "H1", "H2"
        );

        Set<Integer> ignoredColumns = Collections.singleton(3);
        headersReader = new HeadersReader(fakeParser, 0, 1, ignoredColumns);

        checkThatIs(headersReader.read(), data("H1"), date("Date"), time("Time"));
    }

    @Test
    public void read_MissingTimeHeader_ThrowsParseException() throws Exception {
        HeadersParser fakeParser = fakeParser(
                "Date"
        );

        Set<Integer> ignoredColumns = Collections.emptySet();
        headersReader = new HeadersReader(fakeParser, 0, 1, ignoredColumns);

        catcher.expect(ParseException.class);
        catcher.expectMessage("Headers file was expected to have at least 2 header names, but got only 1");
        headersReader.read();

    }

}