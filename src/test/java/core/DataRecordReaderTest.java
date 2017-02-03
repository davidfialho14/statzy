package core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataRecordReaderTest {

    private DataRecordReader recordReader;  // UNIT under test

    @Rule
    public ExpectedException catcher = ExpectedException.none();

    private static List<String> record(String... values) {
        return Arrays.asList(values);
    }

    @SafeVarargs
    private static RecordParser fakeParser(List<String>... records) throws ParseException {

        List<Record> parsingRecords = new ArrayList<>(records.length);
        for (int i = 0; i < records.length; i++) {
            parsingRecords.add(Record.from(records[i], i + 1));
        }

        RecordParser parserStub = mock(RecordParser.class);
        Iterator<Record> recordIterator = parsingRecords.iterator();
        when(parserStub.parseRecord()).thenAnswer(invocationOnMock -> recordIterator.next());

        return parserStub;
    }

    @Test
    public void
    read_RecordWithDateAndTimeInFirstColumnsAnd2Values_DataRecordWithCorrectTimestampAnd2Values() throws Exception {
        RecordParser parserStub = fakeParser(
                record("09/08/2016", "11:22:01", "176", "186"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateInColumn(0)
                .withDatePattern("dd/MM/uuuu")
                .withTimeInColumn(1)
                .withTimePattern("HH:mm:ss")
                .build();

        assertThat(recordReader.read(),
                is(new DataRecord(Timestamp.of(2016, 8, 9, 11, 22, 1), Arrays.asList(176.0, 186.0))));
    }

    @Test
    public void read_RecordWithOnlyDateAndTimeColumns_ThrowsParseException() throws Exception {
        RecordParser parserStub = fakeParser(
                record("09/08/2016", "11:22:01"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateInColumn(0)
                .withDatePattern("dd/MM/uuuu")
                .withTimeInColumn(1)
                .withTimePattern("HH:mm:ss")
                .build();

        catcher.expect(ParseException.class);
        catcher.expectMessage("Record has less values than expected: it is required to have at least 3 " +
                "values, but has only 2.");
        recordReader.read();
    }

    @Test
    public void read_RecordMissingDateColumn_ThrowsParseException() throws Exception {
        RecordParser parserStub = fakeParser(
                record("11:22:01", "123.0"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateInColumn(10)
                .withDatePattern("dd/MM/uuuu")
                .withTimeInColumn(1)
                .withTimePattern("HH:mm:ss")
                .build();

        catcher.expect(ParseException.class);
        catcher.expectMessage("Record has less values than expected: it is required to have at least 11 " +
                "values, but has only 2.");
        recordReader.read();
    }

    @Test
    public void
    read_RecordWithBothDateAndTimeInTheSameValueDelimitedByATab_TimestampIsReadCorrectly() throws Exception {
        RecordParser parserStub = fakeParser(
                record("09/08/2016\t11:22:01", "176"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateAndTimeInColumn(0)
                .delimitedBy(Delimiter.TAB)
                .withDatePattern("dd/MM/uuuu")
                .withTimePattern("HH:mm:ss")
                .build();

        assertThat(recordReader.read(),
                is(new DataRecord(Timestamp.of(2016, 8, 9, 11, 22, 1), Collections.singletonList(176.0))));
    }

    @Test
    public void
    read_RecordWithInvalidDate_ThrowsDateTimeParseException() throws Exception {
        RecordParser parserStub = fakeParser(
                record("09-08-2016", "11:22:01", "176"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateInColumn(0)
                .withDatePattern("dd/MM/uuuu")
                .withTimeInColumn(1)
                .withTimePattern("HH:mm:ss")
                .build();

        catcher.expect(DateTimeParseException.class);
        recordReader.read();
    }

    @Test
    public void
    read_RecordWithDataValueThatIsNotADouble_ThrowsParseException() throws Exception {
        RecordParser parserStub = fakeParser(
                record("09/08/2016", "11:22:01", "A"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateInColumn(0)
                .withDatePattern("dd/MM/uuuu")
                .withTimeInColumn(1)
                .withTimePattern("HH:mm:ss")
                .build();

        catcher.expect(ParseException.class);
        catcher.expectMessage("Expected a number in column 3, but got 'A' instead.");
        recordReader.read();
    }

    @Test
    public void
    read_RecordWithTimeValueBeforeDateValue_ReadsCorrectTimestamp() throws Exception {
        RecordParser parserStub = fakeParser(
                record("11:22:01", "09/08/2016", "176"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateInColumn(1)
                .withDatePattern("dd/MM/uuuu")
                .withTimeInColumn(0)
                .withTimePattern("HH:mm:ss")
                .build();


        assertThat(recordReader.read(),
                is(new DataRecord(Timestamp.of(2016, 8, 9, 11, 22, 1), Collections.singletonList(176.0))));
    }

    @Test
    public void
    read_RecordWithTimeBeforeDateInTheSameValue_ReadsCorrectTimestamp() throws Exception {
        RecordParser parserStub = fakeParser(
                record("11:22:01 09/08/2016", "176"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateAndTimeInColumn(0)
                .withDatePattern("dd/MM/uuuu")
                .withTimePattern("HH:mm:ss")
                .withTimeBeforeDate(true)
                .build();


        assertThat(recordReader.read(),
                is(new DataRecord(Timestamp.of(2016, 8, 9, 11, 22, 1), Collections.singletonList(176.0))));
    }

    @Test
    public void
    read_RecordWithTimeAndDateInTheMiddleOfTheRecord_ReadsCorrectTimestamp() throws Exception {
        RecordParser parserStub = fakeParser(
                record("176", "09/08/2016", "11:22:01", "123"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateInColumn(1)
                .withTimeInColumn(2)
                .withDatePattern("dd/MM/uuuu")
                .withTimePattern("HH:mm:ss")
                .build();

        assertThat(recordReader.read(),
                is(new DataRecord(Timestamp.of(2016, 8, 9, 11, 22, 1), Arrays.asList(176.0, 123.0))));
    }

    @Test
    public void
    read_RecordWith3DataValuesAndTheFirstDataValueIsIgnored_OnlyLastTwoValuesAreRead() throws Exception {
        RecordParser parserStub = fakeParser(
                record("09/08/2016 11:22:01", "176", "123", "142"));

        recordReader = DataRecordReader.with(parserStub)
                .withDateAndTimeInColumn(0)
                .delimitedBy(Delimiter.SPACE)
                .withDatePattern("dd/MM/uuuu")
                .withTimePattern("HH:mm:ss")
                .ignoreColumns(1)
                .build();

        assertThat(recordReader.read(),
                is(new DataRecord(Timestamp.of(2016, 8, 9, 11, 22, 1), Arrays.asList(123.0, 142.0))));
    }

}