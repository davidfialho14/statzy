package core;

import java.util.List;

/**
 * Encapsulates the data contained in a single line of the CSV/Data file.
 * Provides methods to access the values of each data record.
 * Each data row is associated with a timestamp/instant. Date or time columns are not part of the data.
 */
public class DataRecord {

    private Timestamp timestamp;
    private List<Double> dataValues;

    public DataRecord(Timestamp timestamp, List<Double> dataValues) {
        this.timestamp = timestamp;
        this.dataValues = dataValues;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

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
