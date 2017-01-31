package core;

import java.time.temporal.ChronoUnit;

/**
 * Representation of a date/time unit. Replaces the ChronoUnit class, providing methods to truncate a
 * timestamp to years and months.
 */
public enum Unit {

    YEARS(ChronoUnit.YEARS) {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToYears();
        }

    },

    MONTHS(ChronoUnit.MONTHS) {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToMonths();
        }

    },

    DAYS(ChronoUnit.DAYS) {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToDays();
        }

    },

    HOURS(ChronoUnit.HOURS) {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToHours();
        }
    },

    MINUTES(ChronoUnit.MINUTES) {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToMinutes();
        }

    },

    SECONDS(ChronoUnit.SECONDS) {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToSeconds();
        }

    };

    private final ChronoUnit baseUnit;

    Unit(ChronoUnit baseUnit) {
        this.baseUnit = baseUnit;
    }

    ChronoUnit getBaseUnit() {
        return baseUnit;
    }

    abstract Timestamp truncate(Timestamp timestamp);
}
