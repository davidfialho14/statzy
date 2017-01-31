package core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The TimestampFormatter class provides methods to convert timestamps into strings following a predefined
 * pattern and methods to convert a formatted string, following a predefined patter, into a timestamp object.
 */
public class TimestampFormatter {

    private final DateTimeFormatter formatter;

    private TimestampFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Constructs a new timestamp formatter associated with a pattern to be used to formatter a timestamp.
     * The pattern dictates the formatter to be supported by the formatter instance. The supported patterns are
     * based on Java's DatetimeFormatter.
     *
     * @param pattern   pattern to formatter timestamps into and parse from.
     * @return new instant formatter instance.
     */
    public static TimestampFormatter ofPattern(String pattern) {
        return new TimestampFormatter(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parses a string formatted to follow the pattern associated with the formatter. If the formatter of the
     * string is not valid a ParseException is thrown.
     *
     * @param formattedString   formatted string to parse.
     * @return timestamp corresponding to the given string.
     * @throws DateTimeParseException if the provided string does not respect the expected formatter.
     */
    public Timestamp parse(String formattedString) throws DateTimeParseException {
        return new Timestamp(LocalDateTime.parse(formattedString, formatter));
    }

    /**
     * Formats a timestamp into a string, following the pattern provided during the initialization of the
     * of the TimestampInitializer.
     *
     * @param timestamp timestamp object to formatter to string.
     * @return formatted string representing the given timestamp.
     */
    public String format(Timestamp timestamp) {
        return formatter.format(timestamp.dateTime);
    }

}
