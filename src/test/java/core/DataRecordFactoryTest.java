package core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import static core.DataRecordFactory.factoryExpectingItemCountPerRecord;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataRecordFactoryTest {

    private DataRecordFactory dataRecordFactory;

    @Mock
    private DataFileReader.RawRecord mockedRawRecord;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DataFileReader.RawRecord fakeRawRecord(String... items) {
        List<String> fakeItems = Arrays.asList(items);

        // mock the get method to call the get method of the list
        when(mockedRawRecord.get(anyInt())).thenAnswer(invocation -> {
            int index = invocation.getArgument(0);
            return fakeItems.get(index);
        });

        when(mockedRawRecord.size()).thenReturn(fakeItems.size());

        return mockedRawRecord;
    }

    @Test
    public void getDataRecord_ValidDateAndTimeInDifferentColumnsAndTwoValueColumns_GetsCorrectDataRecord() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(4)
                .withDateInColumn(0)
                .withTimeInColumn(1)
                .build();

        Timestamp expectedTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016", "11:22:33", "176", "186")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void getDataRecord_SetFactoryToExpect4ColumnsButRecordHasOnly3_RaisesParseException() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(4)
                .withDateInColumn(0)
                .withTimeInColumn(1)
                .build();

        thrown.expect(ParseException.class);
        thrown.expectMessage("Expected 4 columns but got 3");
        dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016", "11:22:33", "176"));
    }

    @Test
    public void getDataRecord_FromRawRecordWithInvalidDate_RaisesDateTimeParseException() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(4)
                .withDateInColumn(0)
                .withTimeInColumn(1)
                .build();

        thrown.expect(DateTimeParseException.class);
        thrown.expectMessage("Text '09082016 11:22:33' could not be parsed at index 2");
        dataRecordFactory.getDataRecord(fakeRawRecord("09082016", "11:22:33", "176", "183"));
    }

    @Test
    public void getDataRecord_ValueIsNotADouble_RaisesParseException() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(4)
                .withDateInColumn(0)
                .withTimeInColumn(1)
                .build();

        thrown.expect(ParseException.class);
        thrown.expectMessage("Expected a number in column 3 but got 'NOT_DOUBLE'");
        dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016", "11:22:33", "NOT_DOUBLE", "183"));
    }

    @Test
    public void getDataRecord_FromRecordWith4ColumnsWhenFactoryExpects3_ThrowsParseException() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(3)
                .withDateInColumn(0)
                .withTimeInColumn(1)
                .build();

        thrown.expect(ParseException.class);
        thrown.expectMessage("Expected 3 columns but got 4");
        dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016", "11:22:33", "176", "186"));
    }

    @Test
    public void getDataRecord_DateAndTimeInSameColumns_GetsRecordWithCorrectTimestamp() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(3)
                .withDateAndTimeInColumn(0)
                .build();

        Timestamp expectedTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016 11:22:33", "176", "186")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void getDataRecord_HavingTimeBeforeDateInTheSameColumn_GetsRecordWithCorrectTimestamp() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(3)
                .withDateAndTimeInColumn(0)
                .withTimeBeforeDate(true)
                .build();

        Timestamp expectedTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("11:22:33 09/08/2016", "176", "186")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void getDataRecord_HavingTimeAndDateInTheSameColumnDelimitedByTab_GetsRecordWithCorrectTimestamp() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(3)
                .withDateAndTimeInColumn(0)
                .delimitedBy(Delimiter.TAB)
                .build();

        Timestamp expectedTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016\t11:22:33", "176", "186")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void getDataRecord_HavingTimeBeforeDateInDifferentColumns_GetsRecordWithCorrectTimestamp() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(4)
                .withTimeInColumn(0)
                .withDateInColumn(1)
                .build();

        Timestamp expectedTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("11:22:33", "09/08/2016", "176", "186")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void getDataRecord_HavingTimeAndDateInLastColumns_GetsRecordWithCorrectTimestampAndValues() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(4)
                .withTimeInColumn(2)
                .withDateInColumn(3)
                .build();

        Timestamp expectedTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("176", "186", "11:22:33", "09/08/2016")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void
    getDataRecord_HavingDateAndTimeSpecifiedSeparatelyInSameColumn_GetsRecordWithCorrectTimestampAndValues() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(3)
                .withTimeInColumn(0)
                .withDateInColumn(0)
                .delimitedBy(Delimiter.SPACE)
                .build();

        Timestamp expectedTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016 11:22:33", "176", "186")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void getDataRecord_DateAndTimeInDifferentColumnsAndDelimiterIsTab_IgnoresDelimiter() throws Exception {
        dataRecordFactory = factoryExpectingItemCountPerRecord(4)
                .withDateInColumn(0)
                .withTimeInColumn(1)
                .delimitedBy(Delimiter.TAB)
                .build();

        Timestamp expectedTimestamp = Timestamp.of(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016", "11:22:33", "176", "186")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void constructor_SetFactoryWithDateColumnOverTheColumnCount_RaisesParseException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        factoryExpectingItemCountPerRecord(4)
                .withDateInColumn(4)
                .build();
    }

    @Test
    public void constructor_SetFactoryWithColumnCount4AndIgnoredColumn10_RaisesIllegalArgumentException()
            throws Exception {
        thrown.expect(IllegalArgumentException.class);
        factoryExpectingItemCountPerRecord(4)
                .ignoreColumns(4)
                .build();
    }

}