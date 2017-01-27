package core;

import java.time.Instant;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Encapsulates the implementation used to represent the timestamps of each data record.
 */
public class Timestamp implements Comparable<Timestamp> {

    // Timestamp uses Java's Instant class underneath to represent the timestamp instant
    final Instant instant;  // should only be accessed by the TimestampFormatter class

    /**
     * This constructor should only be used by the TimestampFormatter.
     *
     * @param instant instant to represent the timestamp.
     */
    Timestamp(Instant instant) {
        this.instant = instant;
    }

    /**
     * Constructor to create a timestamp manually. This constructor provides and interface to create a
     * timestamp object using the discriminated values for time unit.
     *
     * @param year   calendar year.
     * @param month  month of the year (value between 1-12).
     * @param day    day of the month (value between 1-31).
     * @param hour   hour of day (value between 0-23).
     * @param minute minute of hour (value between 0-59).
     * @param second second of minute (value between 0-59).
     */
    public Timestamp(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        instant = calendar.toInstant();
    }

    /**
     * Checks if the timestamp predates another timestamp.
     *
     * @param other other timestamp.
     * @return true if the timestamp predates the other timestamp.
     */
    public boolean predates(Timestamp other) {
        return this.compareTo(other) < 0;
    }

    @Override
    public int compareTo(Timestamp other) {
        return this.instant.compareTo(other.instant);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timestamp timestamp = (Timestamp) o;

        return instant != null ? instant.equals(timestamp.instant) : timestamp.instant == null;
    }

    @Override
    public int hashCode() {
        return instant != null ? instant.hashCode() : 0;
    }

    /**
     * A string representation of this timestamp using ISO-8601 representation. The format used is the same
     * as DateTimeFormatter.ISO_INSTANT.
     *
     * @return an ISO-8601 representation of this timestamp, not null
     */
    @Override
    public String toString() {
        return instant.toString();
    }

}
