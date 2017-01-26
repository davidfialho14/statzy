package core;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * The TimestampFormatter class provides methods to convert timestamps into strings following a predefined
 * pattern and methods to convert a formatted string, following a predefined patter, into a timestamp object.
 */
public class TimestampFormatter {

    private final SimpleDateFormat format;

    private TimestampFormatter(String pattern) {
        format = new SimpleDateFormat(pattern);
    }

    /**
     * Constructs a new timestamp formatter associated with a pattern to be used to format a timestamp.
     * The pattern dictates the format to be supported by the formatter instance. The supported patterns are
     * based on Java's DatetimeFormatter.
     *
     * @param pattern   pattern to format timestamps into and parse from.
     * @return new instant formatter instance.
     */
    public static TimestampFormatter withPattern(String pattern) {
        return new TimestampFormatter(pattern);
    }

    /**
     * Parses a string formatted to follow the pattern associated with the formatter. If the format of the
     * string is not valid a ParseException is thrown.
     *
     * @param formattedString   formatted string to parse.
     * @return timestamp corresponding to the given string.
     * @throws ParseException if the provided string does not respect the expected format.
     */
    public Timestamp parse(String formattedString) throws ParseException {
        return new Timestamp(format.parse(formattedString).toInstant());
    }

    /**
     * Formats a timestamp into a string, following the pattern provided during the initialization of the
     * of the TimestampInitializer.
     *
     * @param timestamp timestamp object to format to string.
     * @return formatted string representing the given timestamp.
     */
    public String format(Timestamp timestamp) {
        return format.format(Date.from(timestamp.instant));
    }

}
