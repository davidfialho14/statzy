package core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Data record reader reads data records from a data file. It is a layer over a DataParser that takes each
 * parsed record and converts it into a DataRecord.
 */
public class DataRecordReader implements Closeable {

    private final RecordParser parser;
    private final int dateColumn;
    private final int timeColumn;
    private final Set<Integer> ignoredColumns;
    private final TimestampFormatter formatter;

    /**
     * Creates a new data record reader. Specifies the underlying record parser to be used.
     *
     * @param parser the record parser used to parse the file, the null.
     */
    private DataRecordReader(RecordParser parser, int dateColumn, int timeColumn, Set<Integer> ignoredColumns,
                             TimestampFormatter timestampFormatter) {
        this.parser = parser;
        this.dateColumn = dateColumn;
        this.timeColumn = timeColumn;
        this.ignoredColumns = ignoredColumns;
        this.formatter = timestampFormatter;
    }

    /**
     * Reads the next data record from the file. If the file has not more records then it return null.
     *
     * @return the next data record, or null if there is no more records in the file.
     * @throws ParseException
     * @throws DateTimeParseException if the date/time formats are invalid.
     */
    public DataRecord read() throws ParseException {

        // get next record using the parser
        Record record = parser.parseRecord();

        if (record == null) return null;

        // check if the record contains enough values - see documentation for the getMinimumRecordSize()
        int minimumRecordSize = getMinimumRecordSize();
        if (record.size() < minimumRecordSize) {
            throw new ParseException("Record has less values than expected: it is required to have " +
                    "at least " + minimumRecordSize + " values, but has only " + record.size() + ".",
                    record.getRecordNumber());
        }

        String timestampString;
        if (dateColumn != timeColumn) {
            timestampString = record.get(dateColumn) + Delimiter.DEFAULT + record.get(timeColumn);
        } else {
            timestampString = record.get(dateColumn);
        }

        Timestamp timestamp = formatter.parse(timestampString);

        // Build a list with each value that does not correspond to an ignored column (date and time are
        // also ignored) - values are parsed into doubles
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < record.size(); i++) {

            // Don't need to check date and time columns because the ignored columns set
            // already includes the date and time columns
            if (!ignoredColumns.contains(i)) {  // exclude ignored columns

                try {
                    values.add(Double.parseDouble(record.get(i)));

                } catch (NumberFormatException e) {
                    throw new ParseException("Expected a number in column " + (i + 1) + ", but got '" +
                            record.get(i) + "' instead.", record.getRecordNumber());
                }
            }
        }

        return new DataRecord(timestamp, values);

    }

    /**
     * Closes the underlying record parser.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        parser.close();
    }

    /**
     * The minimum record size corresponds to having the date and time value(s) and at least one data value.
     *
     * @return the minimum record size.
     */
    private int getMinimumRecordSize() {
        if (dateColumn == timeColumn) {
            return Math.max(dateColumn + 1, 2);
        } else {
            return Math.max(3, Math.max(dateColumn + 1, timeColumn + 1));
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Builder class
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Factory method to create a builder for a data record reader. Always use this method to start
     * building a new data record reader.
     *
     * @return builder to build a configure the data record factory.
     */
    public static Builder with(File file) {
        return new Builder(file);
    }

    public static Builder with(RecordParser parser) {
        return new Builder(parser);
    }

    /**
     * Builder class for the DataRecordReader.
     */
    public static class Builder {

        private final RecordParser parser;
        private File file;

        private int dateColumn = 0;
        private int timeColumn = 1;
        private Set<Integer> ignoredColumns = new HashSet<>();
        private String datePattern = "dd/MM/uuuu";
        private String timePattern = "HH:mm:ss";
        private Delimiter delimiter = Delimiter.DEFAULT;    // delimiter for the date and time patterns
        private boolean timeBeforeDate = false;             // indicates if the time comes before the date

        // use factory method
        private Builder(File file) {
            this.file = file;
            this.parser = null;
        }

        private Builder(RecordParser parser) {
            this.parser = parser;
            this.file = null;
        }

        public Builder withDateInColumn(int column) {
            dateColumn = column;
            return this;
        }

        public Builder withTimeInColumn(int column) {
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
            ignoredColumns.clear();
            Collections.addAll(ignoredColumns, columns);
            return this;
        }

        public Builder ignoreColumns(Collection<Integer> columns) {
            ignoredColumns.clear();
            this.ignoredColumns.addAll(columns);
            return this;
        }

        public void addIgnoredColumn(Integer column) {
            ignoredColumns.add(column);
        }

        public void setFile(File file) {
            this.file = file;
        }

        public DataRecordReader build() throws IOException {

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

            // ensure the date and time columns are included in the ignored columns
            ignoredColumns.add(dateColumn);
            ignoredColumns.add(timeColumn);

            RecordParser recordParser = parser != null ? parser : new RecordParser(file);
            return new DataRecordReader(recordParser, dateColumn, timeColumn, ignoredColumns,
                    TimestampFormatter.ofPattern(timestampPattern));
        }

    }
}
