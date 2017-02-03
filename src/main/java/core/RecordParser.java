package core;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.text.ParseException;
import java.util.Iterator;

/**
 * Record parser is responsible for parsing a CSV file. Each line in the file is handled as a record. The
 * values of each record correspond to the 'columns' of the CSV file.
 */
public class RecordParser implements Closeable {

    private static final char DELIMITER = ',';

    private final CSVParser csvParser;
    private final Iterator<CSVRecord> recordIterator;

    private boolean isFirstRecord = true;
    private int expectedColumnCount = 0;

    /**
     * Creates a new record parser.
     *
     * @param reader the reader used to read the strings from the file, not null.
     * @throws IOException if an IO error occurs.
     */
    public RecordParser(Reader reader) throws IOException {
        csvParser = CSVFormat.EXCEL.withDelimiter(DELIMITER).parse(reader);
        recordIterator = csvParser.iterator();
    }

    /**
     * Creates a new record parser.
     *
     * @param file the file to be parsed, not null.
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if an IO error occurs.
     */
    public RecordParser(File file) throws IOException {
        this(new FileReader(file));
    }

    /**
     * Parses the next record in the file. Each line in the file is recognized as a record.
     * Therefore, calling this method is the same as reading the next non-empty line in a file. Empty lines
     * are ignored.  If the file does not have anymore non-empty lines, then it returns null.
     *
     * @return the next data record in the file or null if the file does not have anymore non-empty lines.
     * @throws ParseException if the number of values is the next row is different from the previous record
     * parsed. If it is the first record then it never throws a ParseException.
     */
    public Record parseRecord() throws ParseException {
        Record record = null;

        while (recordIterator.hasNext() && record == null) {
            // read record
            CSVRecord csvRecord = recordIterator.next();

            if (isEmptyRecord(csvRecord)) {
                // ignore empty records
                continue;
            }

            if (isFirstRecord) {
                // set the expected column count based on the first record
                expectedColumnCount = csvRecord.size();
                isFirstRecord = false;

            } else if (csvRecord.size() != expectedColumnCount) {
                // this record has different number of columns than expected
                throw new ParseException("Record has " + csvRecord.size() + " values, but " +
                        expectedColumnCount + " values were expected.", (int) csvRecord.getRecordNumber());
            }

            record = Record.from(csvRecord);
        }

        return record;
    }

    /**
     * Closes the underlying CSV parser.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        csvParser.close();
    }

    /**
     * Checks if the CSV record corresponds to an empty line.
     *
     * @param csvRecord te csv record to check for.
     * @return true if the record is an empty line, and false if otherwise.
     */
    private static boolean isEmptyRecord(CSVRecord csvRecord) {
        return csvRecord.size() == 1 && csvRecord.get(0).trim().isEmpty();
    }

}
