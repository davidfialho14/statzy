package core;

import java.text.ParseException;
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

    public DataRecordFactory(int columnCount, int dateColumn, int timeColumn,
                             Collection<Integer> ignoredColumns, TimestampFormatter formatter) {

        if (dateColumn >= columnCount || timeColumn >= columnCount) {
            throw new IllegalArgumentException("Date/time column number is higher then the number of " +
                    "expected columns");
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
     */
    public DataRecord getDataRecord(DataFileReader.RawRecord record) throws ParseException {

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

}
