package core;

import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates the data contained in a single line of the CSV/Data file.
 * Provides methods to access the values of each data record.
 * Each data row is associated with a timestamp. Date or time columns are not part of the data.
 */
public class DataRecord {

    private Timestamp timestamp;
    private List<Double> dataValues;

    /**
     * Creates a data record associated with a timestamp and with a sequence of data values.
     * This constructor should not be called directly. Instead the DataRecordFactory should be used.
     *
     * @param timestamp  timestamp to associate with the record.
     * @param dataValues data values for the record.
     */
    public DataRecord(Timestamp timestamp, List<Double> dataValues) {
        this.timestamp = timestamp;
        this.dataValues = dataValues;
    }

    /**
     * Creates a data record associated with a timestamp and with a sequence of data values. This factory
     * method is just a wrapper around the constructor to statically create records in a more readable way.
     * It simply converts the dataValues from array to list.
     *
     * @param timestamp  timestamp to associate with the record.
     * @param dataValues data values for the record.
     */
    public static DataRecord dataRecord(Timestamp timestamp, Double... dataValues) {
        return new DataRecord(timestamp, Arrays.asList(dataValues));
    }

    /**
     * Returns the timestamp for this record.
     *
     * @return the timestamp for this record.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Returns a list containing the data values in this record. The values are returned in the list in the
     * same order they appear on the file.
     *
     * @return list containing the data values in this record.
     */
    public List<Double> getDataValues() {
        return dataValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataRecord that = (DataRecord) o;

        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        return dataValues != null ? dataValues.equals(that.dataValues) : that.dataValues == null;
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (dataValues != null ? dataValues.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Record(" + timestamp + ", " + dataValues + ")";
    }

}
