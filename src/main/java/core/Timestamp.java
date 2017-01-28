package core;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;

import static java.time.temporal.ChronoUnit.*;

/**
 * Encapsulates the implementation used to represent the timestamps of each data record.
 */
public class Timestamp implements Comparable<Timestamp> {

    public static final Timestamp MIN = new Timestamp(LocalDateTime.MIN);

    // Timestamp uses Java's Instant class underneath to represent the timestamp instant
    final LocalDateTime dateTime;  // should only be accessed by the TimestampFormatter class

    /**
     * This constructor should only be used by the TimestampFormatter.
     *
     * @param dateTime instant to represent the timestamp.
     */
    Timestamp(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
        dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
    }

    private static final EnumMap<ChronoUnit, ChronoUnit> truncateUnits = new EnumMap<>(ChronoUnit.class);
    static {
        truncateUnits.put(SECONDS, MINUTES);
        truncateUnits.put(MINUTES, HOURS);
        truncateUnits.put(HOURS, DAYS);
    }

    public Timestamp truncateTo(Period period) {

        ChronoUnit truncateUnit = truncateUnits.get(period.getUnit());

//        if (truncateUnit == null) {
//            int month = instant.get(period.getField());
//            int interval = month / period.getDuration();
//            return new Timestamp(instant
//                    .truncatedTo(DAYS)
//                    .with(period.getField(), interval * period.getDuration()));
//
//        } else {
//            Instant lowerBound = instant.truncatedTo(truncateUnit);
//            Instant upperBound = lowerBound.plus(period.getDuration(), period.getUnit());
//
//            while (true) {
//                if (instant.isBefore(upperBound)) {
//                    return new Timestamp(lowerBound);
//                }
//
//                lowerBound = upperBound;
//                upperBound = upperBound.plus(period.getDuration(), period.getUnit());
//            }
//        }

        return null;

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
        return this.dateTime.compareTo(other.dateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timestamp timestamp = (Timestamp) o;

        return dateTime != null ? dateTime.equals(timestamp.dateTime) : timestamp.dateTime == null;
    }

    @Override
    public int hashCode() {
        return dateTime != null ? dateTime.hashCode() : 0;
    }

    /**
     * A string representation of this timestamp using ISO-8601 representation. The format used is the same
     * as DateTimeFormatter.ISO_INSTANT.
     *
     * @return an ISO-8601 representation of this timestamp, not null
     */
    @Override
    public String toString() {
        return dateTime.toString();
    }

}
