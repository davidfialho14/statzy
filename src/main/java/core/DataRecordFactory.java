package core;

import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Factory to create data records.
 * Holds information relative to the format expected for the date and time, including indication if the date
 * and time are in different columns or in the same column.
 */
public class DataRecordFactory {

    private final int columnCount;  // this number includes date, time, and ignored columns
    private final int dateColumn;
    private final int timeColumn;
    private final Set<Integer> ignoredColumns;
    private final TimestampFormatter formatter;

    /**
     * The constructor should not be used by client. Instead use the builder class starting with the
     * factory factory method {@link #factoryExpectingItemCountPerRecord(int)}.
     * The constructor does not check the arguments. Expects the builder class to do it.
     */
    private DataRecordFactory(int columnCount, int dateColumn, int timeColumn,
                             Collection<Integer> ignoredColumns, TimestampFormatter formatter) {

        for (Integer ignoredColumn : ignoredColumns) {

            if (ignoredColumn >= columnCount) {
                throw new IllegalArgumentException("Ignored columns set includes an invalid column: " +
                        "expected to have only " + columnCount + " and ignored columns set includes column " +
                        "" + ignoredColumn);
            }
        }


        this.columnCount = columnCount;
        this.dateColumn = dateColumn;
        this.timeColumn = timeColumn;
        this.formatter = formatter;
        this.ignoredColumns = new HashSet<>(ignoredColumns);

        // include the date and time columns in the ignored columns set
        this.ignoredColumns.add(dateColumn);
        this.ignoredColumns.add(timeColumn);
    }

    /**
     * Constructs a data record from a raw record and based on pre-configurations defined with the factory.
     *
     * @param record raw record containing the data and timestamp to create a data record.
     * @return new data record instance initialized with a timestamp and a set of values from the raw record.
     * @throws ParseException if the record is missing the timestamp (missing the date or time), or if at
     * least one of the values is not a double value, or if the number of values is not the expected.
     * @throws DateTimeParseException if the date or time could not be parsed.
     */
    public DataRecord getDataRecord(DataFileReader.RawRecord record)
            throws DateTimeParseException, ParseException {

        if (record.size() != columnCount) {
            throw new ParseException("Expected " + columnCount + " columns but got " + record.size(),
                    record.getRecordNumber());
        }

        String timestampString;
        if (dateColumn != timeColumn) {
            timestampString = record.get(dateColumn) + Delimiter.DEFAULT + record.get(timeColumn);
        } else {
            timestampString = record.get(dateColumn);
        }

        Timestamp timestamp = formatter.parse(timestampString);

        // the ignored columns set already includes the date and time columns
        List<Double> values = new ArrayList<>(columnCount - ignoredColumns.size());

        for (int i = 0; i < record.size(); i++) {

            if (!ignoredColumns.contains(i)) {
                try {
                    values.add(Double.parseDouble(record.get(i)));
                } catch (NumberFormatException e) {
                    throw new ParseException("Expected a number in column " + (i + 1) + " but got '" +
                            record.get(i) + "'", record.getRecordNumber());
                }
            }
        }

        return new DataRecord(timestamp, values);
    }

    /**
     * Returns the expected number of data values.
     *
     * @return the expected number of data values.
     */
    public int getExpectedValueCount() {
        return columnCount - ignoredColumns.size();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Builder class
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Factory method to create a builder for a data record factory. Always use this method to start
     * building a new data record factory.
     *
     * @param columnCount   number of items expected in a record (includes date, time, and ignored columns).
     * @return builder to build a configure the data record factory.
     */
    public static Builder factoryExpectingItemCountPerRecord(int columnCount) {
        return new Builder(columnCount);
    }

    /**
     * Builder class for the DataRecordFactory.
     * Mandatory parameters:
     *  - column count
     *
     * Optional parameters:
     *  - date column: default is 0
     *  - time column: default is 1
     *  - ignored columns: default is an empty list
     *  - timestamp pattern: default 'dd/MM/yyyy HH:mm:ss'
     */
    public static class Builder {

        private final int columnCount;

        private int dateColumn = 0;
        private int timeColumn = 1;
        private Set<Integer> ignoredColumns = new HashSet<>();
        private String datePattern = "dd/MM/uuuu";
        private String timePattern = "HH:mm:ss";
        private Delimiter delimiter = Delimiter.DEFAULT;    // delimiter for the date and time patterns
        private boolean timeBeforeDate = false;             // indicates if the time comes before the date

        // use the factory method above
        private Builder(int columnCount) {
            this.columnCount = columnCount;
        }

        public Builder withDateInColumn(int column) {

            if (column >= columnCount) {
                throw new IllegalArgumentException("Date column number can not be higher than the total " +
                        "number of columns");
            }

            dateColumn = column;
            return this;
        }

        public Builder withTimeInColumn(int column) {

            if (column >= columnCount) {
                throw new IllegalArgumentException("Time column number can not be higher than the total " +
                        "number of columns");
            }

            timeColumn = column;
            return this;
        }

        public Builder withDateAndTimeInColumn(int column) {
            withDateInColumn(column);
            withTimeInColumn(column);
            return this;
        }

        public Builder withDatePattern(String pattern) {
            datePattern = pattern;
            return this;
        }

        public Builder withTimePattern(String pattern) {
            timePattern = pattern;
            return this;
        }

        public Builder delimitedBy(Delimiter delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public Builder withTimeBeforeDate(boolean timeBeforeDate) {
            this.timeBeforeDate = timeBeforeDate;
            return this;
        }

        public Builder ignoreColumns(Integer... columns) {
            Collections.addAll(ignoredColumns, columns);
            return this;
        }

        public Builder ignoreColumns(Collection<Integer> columns) {
            this.ignoredColumns.addAll(columns);
            return this;
        }

        public DataRecordFactory build() {

            String timestampPattern;
            if (dateColumn == timeColumn) {
                if (timeBeforeDate) {
                    timestampPattern = timePattern + delimiter + datePattern;
                } else {
                    timestampPattern = datePattern + delimiter + timePattern;
                }

            } else {
                timestampPattern = datePattern + Delimiter.DEFAULT + timePattern;
            }

            return new DataRecordFactory(columnCount, dateColumn, timeColumn, ignoredColumns,
                    TimestampFormatter.ofPattern(timestampPattern));
        }

    }

}
