package core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.ParseException;
import java.time.format.DateTimeParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TimestampFormatterTest {

    private TimestampFormatter formatter = TimestampFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parse_ValidFormattedString_CorrespondingTimestamp() throws Exception {

        assertThat(formatter.parse("09/12/2016 15:10:11"),
                is(Timestamp.of(2016, 12, 9, 15, 10, 11)));
    }

    @Test
    public void parse_NumbersAreNotPrefixedByZero_ThrowsDateTimeParseException() throws Exception {

        thrown.expect(DateTimeParseException.class);

        assertThat(formatter.parse("9/1/2016 1:2:3"),
                is(Timestamp.of(2016, 1, 9, 1, 2, 3)));
    }

    @Test
    public void parse_EmptyString_ThrowsDateTimeParseException() throws Exception {

        thrown.expect(DateTimeParseException.class);

        assertThat(formatter.parse(""),
                is(Timestamp.of(2016, 10, 9, 1, 10, 11)));
    }

    @Test
    public void parse_InvalidFormatString_ThrowsDateTimeParseException() throws Exception {

        thrown.expect(DateTimeParseException.class);

        assertThat(formatter.parse("15:10:11 09/12/2016"),
                is(Timestamp.of(2016, 10, 9, 1, 10, 11)));
    }

    @Test
    public void format_ValidFormattedString_CorrespondingTimestamp() throws Exception {

        assertThat(formatter.format(Timestamp.of(2016, 1, 9, 5, 3, 2)),
                is("09/01/2016 05:03:02"));
    }

}