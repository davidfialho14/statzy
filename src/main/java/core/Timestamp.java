package core;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    public static Timestamp of(int year, int month, int day, int hour, int minute, int second) {
        return new Timestamp(LocalDateTime.of(year, month, day, hour, minute, second));
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Public Interface
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * This method returns the primitive int value for the year.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR.
     */
    public int getYear() {
        return dateTime.getYear();
    }

    /**
     * This method returns the month as an int from 1 to 12
     *
     * @return the month, from 1 to 12.
     */
    public int getMonth() {
        return dateTime.getMonthValue();
    }

    /**
     * This method returns the primitive int value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 31.
     */
    public int getDay() {
        return dateTime.getDayOfMonth();
    }

    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    public int getHour() {
        return dateTime.getHour();
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinute() {
        return dateTime.getMinute();
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecond() {
        return dateTime.getSecond();
    }

    /**
     * Returns a copy of this Timestamp with the date and time truncated. Truncation returns a copy of the
     * original date-time with fields smaller than the specified unit set to zero. Day and month fields are
     * truncated to 1.
     *
     * @param unit the unit to truncate to, not null.
     * @return copy of this timestamp with the date and time truncated, not null.
     */
    public Timestamp truncatedTo(Unit unit) {
        return unit.truncate(this);
    }

    public Timestamp plus(Period period) {
        return period.addTo(this);
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

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Truncation visitor methods, visited by the Unit objects to truncate a timestamp.
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    Timestamp truncatedToYears() {
        return Timestamp.of(dateTime.getYear(), 1, 1, 0, 0, 0);
    }

    Timestamp truncatedToMonths() {
        return Timestamp.of(dateTime.getYear(), dateTime.getMonthValue(), 1, 0, 0, 0);
    }

    Timestamp truncatedToDays() {
        return new Timestamp(dateTime.truncatedTo(ChronoUnit.DAYS));
    }

    Timestamp truncatedToHours() {
        return new Timestamp(dateTime.truncatedTo(ChronoUnit.HOURS));
    }

    Timestamp truncatedToMinutes() {
        return new Timestamp(dateTime.truncatedTo(ChronoUnit.MINUTES));
    }

    Timestamp truncatedToSeconds() {
        return new Timestamp(dateTime.truncatedTo(ChronoUnit.SECONDS));
    }

}
