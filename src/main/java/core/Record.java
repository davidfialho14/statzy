package core;

import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Record corresponds to a row in a CSV file. The row will have multiple values (corresponding to different
 * columns). Each value is stored as a string. A record also stores the line where the records is stored in
 * the file.
 */
public class Record implements Iterable<String> {

    private final List<String> values;
    private final int recordNumber;

    private Record(List<String> values, int recordNumber) {
        this.values = values;
        this.recordNumber = recordNumber;
    }

    /**
     * Creates a record from a CSV record. SHOULD ONLY be used by the RecordParser class.
     *
     * @param csvRecord the csv record to be loaded into a record, not null.
     * @return the new record, not null.
     */
    static Record from(CSVRecord csvRecord) {
        List<String> values = new ArrayList<>(csvRecord.size());
        csvRecord.forEach(value -> values.add(value.trim()));

        return new Record(values, (int) csvRecord.getRecordNumber());
    }

    /**
     * Creates a new record. Specifies the values for the record and the number of the record.
     *
     * @param values the values of the record, not null.
     * @param recordNumber the line number of the record.
     * @return the new record, not null.
     */
    public static Record from(List<String> values, int recordNumber) {
        return new Record(values, recordNumber);
    }

    /**
     * Obtains a value using its index (column number). The index starts at zero.
     *
     * @param index the index of the value to obtain.
     * @return the value.
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size()).
     */
    public String get(int index) {
        return values.get(index);
    }

    /**
     * Returns the line number where the record is stored.
     *
     * @return the record's line number.
     */
    public int getRecordNumber() {
        return recordNumber;
    }

    /**
     * Returns the number of values the record holds.
     *
     * @return the number of values the record holds.
     */
    public int size() {
        return values.size();
    }

    /**
     * Returns an iterator over the values in the record.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<String> iterator() {
        return null;
    }

    /**
     * Two records are equal if they have the same values in the same exact order and the same record number.
     *
     * @param o the other record.
     * @return true if both records have the same values and the same number, and false if otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record strings = (Record) o;

        if (recordNumber != strings.recordNumber) return false;
        return values != null ? values.equals(strings.values) : strings.values == null;
    }

    @Override
    public int hashCode() {
        int result = values != null ? values.hashCode() : 0;
        result = 31 * result + recordNumber;
        return result;
    }

    /**
     *
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Record(" + recordNumber + ", values=" + values + ')';
    }

}
