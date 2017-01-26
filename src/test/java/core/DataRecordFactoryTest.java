package core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataRecordFactoryTest {

    private DataRecordFactory dataRecordFactory;
    private final TimestampFormatter formatter = TimestampFormatter.withPattern("dd/MM/yyyy HH:mm:ss");

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
        dataRecordFactory = new DataRecordFactory(4, 0, 1, emptyList(), formatter);

        Timestamp expectedTimestamp = new Timestamp(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016", "11:22:33", "176", "186")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void getDataRecord_SetFactoryToExpect4ColumnsButRecordHasOnly3_RaisesParseException() throws Exception {
        dataRecordFactory = new DataRecordFactory(4, 0, 1, emptyList(), formatter);

        thrown.expect(ParseException.class);
        thrown.expectMessage("Expected 4 columns but got 3");
        dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016", "11:22:33", "176"));
    }

    @Test
    public void getDataRecord_FromRawRecordWithInvalidDate_RaisesParseException() throws Exception {
        dataRecordFactory = new DataRecordFactory(4, 0, 1, emptyList(), formatter);

        thrown.expect(ParseException.class);
        thrown.expectMessage("Unparseable date: \"09082016 11:22:33\"");
        dataRecordFactory.getDataRecord(fakeRawRecord("09082016", "11:22:33", "176", "183"));
    }

    @Test
    public void getDataRecord_ValueIsNotADouble_RaisesParseException() throws Exception {
        dataRecordFactory = new DataRecordFactory(4, 0, 1, emptyList(), formatter);

        thrown.expect(ParseException.class);
        thrown.expectMessage("Expected a number in column 3 but got 'NOT_DOUBLE'");
        dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016", "11:22:33", "NOT_DOUBLE", "183"));
    }

    @Test
    public void getDataRecord_DateAndTimeInDifferentColumns_GetsRecordWithCorrectTimestamp() throws Exception {
        dataRecordFactory = new DataRecordFactory(3, 0, 0, emptyList(), formatter);

        Timestamp expectedTimestamp = new Timestamp(2016, 8, 9, 11, 22, 33);
        List<Double> expectedValues = Arrays.asList(176.0, 186.0);

        assertThat(dataRecordFactory.getDataRecord(fakeRawRecord("09/08/2016 11:22:33", "176", "186")),
                is(new DataRecord(expectedTimestamp, expectedValues)));
    }

    @Test
    public void constructor_SetFactoryWithDateColumnOverTheColumnCount_RaisesParseException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        new DataRecordFactory(4, 4, 1, emptyList(), formatter);
    }

}