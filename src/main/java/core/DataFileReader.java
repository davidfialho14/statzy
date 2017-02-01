package core;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.text.ParseException;
import java.util.Iterator;

/**
 * Abstraction to read data files.
 */
public class DataFileReader implements Closeable {

    private final CSVParser parser;
    private final Iterator<CSVRecord> recordIterator;
    private final DataRecordFactory dataRecordFactory;

    private Timestamp previousTimestamp = Timestamp.MIN;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Constructors
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public DataFileReader(Reader reader, DataRecordFactory dataRecordFactory) throws IOException {
        parser = CSVFormat.EXCEL
                .withDelimiter(',')
                .parse(reader);

        recordIterator = parser.iterator();
        this.dataRecordFactory = dataRecordFactory;
    }

    public DataFileReader(File csvFile, DataRecordFactory dataRecordFactory) throws IOException {
        this(new FileReader(csvFile), dataRecordFactory);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Public Interface
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Returns the expected number of data values in each data record.
     *
     * @return the expected number of data values in each data record.
     */
    public int getExpectedValueCount() {
        return dataRecordFactory.getExpectedValueCount();
    }

    /**
     * Reads the next data record in the file. Each line in the file is recognized as as data record.
     * Therefore, calling this method is the same as reading the next line in a file that is not empty.
     * Ignores all empty lines in the file. If the file does not have anymore non-empty lines, then it
     * returns null.
     *
     * @return the next data record in the file or null if the file does not have anymore non-empty lines.
     * @throws ParseException if the record is missing the timestamp (missing the date or time), or
     * if at least one of the values is not a double value, or if the number of values is not the expected,
     * or if fails to parse date and/or time, of the timestamp of the record predates the timestamp of the
     * previous record.
     */
    public DataRecord read() throws ParseException {
        DataRecord record = null;

        while (recordIterator.hasNext() && record == null) {

            try {
                RawRecord rawRecord = RawRecord.wrap(recordIterator.next());
                record = dataRecordFactory.getDataRecord(rawRecord);

                if (record.getTimestamp().predates(previousTimestamp)) {
                    throw new ParseException("Timestamp in line " + rawRecord.getRecordNumber() +
                            " predates the timestamp in the previous record", rawRecord.getRecordNumber());
                }

                previousTimestamp = record.getTimestamp();

            } catch (EmptyRecordException e) {
                // ignore empty records
            }
        }

        return record;
    }

    /**
     * Closes the base CSV parser.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        parser.close();
    }

    /**
     * Wrapper around a CSVRecord. Abstracts the use of the CSV commons library. It also facilitates
     * testing since the CSVRecord class is final, and therefore, can not be mocked.
     *
     * A raw record does not provide any parsing of the data. Items are kept as strings.
     */
    static class RawRecord implements Iterable<String> {

        private final CSVRecord baseRecord;

        private RawRecord(CSVRecord baseRecord) {
            this.baseRecord = baseRecord;
        }

        /**
         * Creates a raw record from a CSV record. Checks if the record is empty and if it is throws an
         * EmptyRecordException.
         *
         * @param csvRecord csv record to wrap in raw record.
         * @return new raw record instance wrapping the csv record.
         * @throws EmptyRecordException if the CSV record is empty: has a single column with no value.
         */
        private static RawRecord wrap(CSVRecord csvRecord) throws EmptyRecordException {

            if (csvRecord.size() == 1 && csvRecord.get(0).trim().isEmpty()) {
                throw new EmptyRecordException("Found empty record", (int) csvRecord.getRecordNumber());
            }

            return new RawRecord(csvRecord);
        }

        public String get(int index) {
            return baseRecord.get(index).trim();
        }

        public int size() {
            return baseRecord.size();
        }

        public int getRecordNumber() {
            return (int) baseRecord.getRecordNumber();
        }

        @Override
        public Iterator<String> iterator() {
            return baseRecord.iterator();
        }
    }

}
