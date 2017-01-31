package core;

/**
 * Representation of a date/time unit. Replaces the ChronoUnit class, providing methods to truncate a
 * timestamp to years and months.
 */
public enum Unit {

    YEARS {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToYears();
        }

    },

    MONTHS {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToMonths();
        }

    },

    DAYS {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToDays();
        }

    },

    HOURS {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToHours();
        }

    },

    MINUTES {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToMinutes();
        }

    },

    SECONDS {

        @Override
        Timestamp truncate(Timestamp timestamp) {
            return timestamp.truncatedToSeconds();
        }

    };

    abstract Timestamp truncate(Timestamp timestamp);
}
