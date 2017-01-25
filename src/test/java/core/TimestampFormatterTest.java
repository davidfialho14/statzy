package core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TimestampFormatterTest {

    private TimestampFormatter formatter = TimestampFormatter.withPattern("dd/MM/yyyy HH:mm:ss");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parse_ValidFormattedString_CorrespondingTimestamp() throws Exception {

        assertThat(formatter.parse("09/12/2016 15:10:11"),
                is(new Timestamp(2016, 12, 9, 15, 10, 11)));
    }

    @Test
    public void parse_NumbersAreNotPrefixedByZero_CorrespondingTimestamp() throws Exception {

        assertThat(formatter.parse("9/1/2016 1:2:3"),
                is(new Timestamp(2016, 1, 9, 1, 2, 3)));
    }

    @Test
    public void parse_EmptyString_ThrowsParseException() throws Exception {

        thrown.expect(ParseException.class);

        assertThat(formatter.parse(""),
                is(new Timestamp(2016, 10, 9, 1, 10, 11)));
    }

    @Test
    public void parse_InvalidFormatString_ThrowsParseException() throws Exception {

        thrown.expect(ParseException.class);

        assertThat(formatter.parse("15:10:11 09/12/2016"),
                is(new Timestamp(2016, 10, 9, 1, 10, 11)));
    }

    @Test
    public void format_ValidFormattedString_CorrespondingTimestamp() throws Exception {

        assertThat(formatter.format(new Timestamp(2016, 12, 9, 15, 10, 11)),
                is("09/12/2016 15:10:11"));
    }

}