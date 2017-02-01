package core;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Abstraction to write data files.
 */
public class DataFileWriter implements Closeable {

    private static final char DELIMITER = ',';
    private final CSVPrinter printer;
    private final TimestampFormatter dateFormatter;
    private final TimestampFormatter timeFormatter;
    private final Delimiter delimiter;  // might be null: indicates the date and time are in different columns
    private final boolean timeBeforeDate;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Constructors
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public DataFileWriter(Writer writer, TimestampFormatter dateFormatter, TimestampFormatter timeFormatter,
                          Delimiter delimiter, boolean timeBeforeDate) throws IOException {

        printer = CSVFormat.EXCEL
                .withDelimiter(DELIMITER)
                .print(writer);

        this.dateFormatter = dateFormatter;
        this.timeFormatter = timeFormatter;
        this.delimiter = delimiter;
        this.timeBeforeDate = timeBeforeDate;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Public Interface
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Writes a record into the output file.
     *
     * @param timestamp the timestamp for the record.
     * @param count     the number of records in the data.
     * @param means     the means for each data set.
     * @param standardDeviations the standard deviations for each data set.
     * @throws IOException if an IO error occurs.
     * @throws IllegalArgumentException if the number of values in the means list is different from the std
     * deviations list.
     */
    public void write(Timestamp timestamp, long count, List<Double> means, List<Double> standardDeviations)
            throws IOException {

        if (means.size() != standardDeviations.size()) {
            throw new IllegalArgumentException("The lists with the mean values and the standard deviations " +
                    "do not have the same number of values");
        }

        String date = dateFormatter.format(timestamp);
        String time = timeFormatter.format(timestamp);

        if (delimiter == null) {
            if (timeBeforeDate) {
                printer.print(time);
                printer.print(date);
            } else {
                printer.print(date);
                printer.print(time);
            }

        } else {
            printer.print(date + delimiter + time);
        }

        printer.print(count);

        Iterator<Double> meansIterator = means.iterator();
        Iterator<Double> standardDeviationsIterator = standardDeviations.iterator();

        while (meansIterator.hasNext()) {   // while
            printer.print(meansIterator.next());
            printer.print(standardDeviationsIterator.next());
        }

        printer.println();
    }

    /**
     * Closes the underlying printer.
     *
     * @throws IOException if an IO error occurs.
     */
    @Override
    public void close() throws IOException {
        printer.close();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Builder class
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static Builder outputTo(Writer writer) {
        return new Builder(writer);
    }

    public static Builder outputTo(File outputFile) throws IOException {
        return new Builder(new FileWriter(outputFile));
    }

    public static class Builder {

        private final Writer writer;

        private String datePattern = "dd/MM/uuuu";
        private String timePattern = "HH:mm:ss";
        private boolean sameColumn = false;
        private Delimiter delimiter = Delimiter.DEFAULT;
        private boolean timeBeforeDate = false;

        private Builder(Writer writer) {
            this.writer = writer;
        }

        public Builder withDatePattern(String pattern) {
            datePattern = pattern;
            return this;
        }

        public Builder withTimePattern(String pattern) {
            timePattern = pattern;
            return this;
        }

        public Builder inSameColumn(boolean sameColumn) {
            this.sameColumn = sameColumn;
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

        public DataFileWriter build() throws IOException {

            return new DataFileWriter(writer,
                    TimestampFormatter.ofPattern(datePattern),
                    TimestampFormatter.ofPattern(timePattern),
                    sameColumn ? delimiter : null,
                    timeBeforeDate);
        }

    }

}
