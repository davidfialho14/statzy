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

        @Override
        public String toString() {
            return "Year(s)";
        }
    },

    MONTHS(ChronoUnit.MONTHS) {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToMonths();
        }

        @Override
        public String toString() {
            return "Month(s)";
        }
    },

    DAYS(ChronoUnit.DAYS) {
        @Override
        public String toString() {
            return "Day(s)";
        }
    },

    HOURS(ChronoUnit.HOURS) {
        @Override
        public String toString() {
            return "Hour(s)";
        }
    },

    MINUTES(ChronoUnit.MINUTES) {
        @Override
        public String toString() {
            return "Minute(s)";
        }
    },

    SECONDS(ChronoUnit.SECONDS) {
        @Override
        public String toString() {
            return "Second(s)";
        }
    };

    private final ChronoUnit baseUnit;

    Unit(ChronoUnit baseUnit) {
        this.baseUnit = baseUnit;
    }

    ChronoUnit getBaseUnit() {
        return baseUnit;
    }

    Timestamp truncate(Timestamp timestamp) {
        return timestamp.truncatedTo(getBaseUnit());
    }
}
