package core;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Abstraction to write data files.
 */
public class DataFileWriter implements Closeable {

    private static final char DELIMITER = ',';
    private static final List<String> STATS_HEADERS = Arrays.asList("Avg", "StdDev");

    private final CSVPrinter printer;
    private final TimestampFormatter dateFormatter;
    private final TimestampFormatter timeFormatter;
    private final Delimiter delimiter;  // might be null: indicates the date and time are in different columns
    private final boolean timeBeforeDate;
    private final int dataSetCount;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Constructors
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public DataFileWriter(Writer writer, TimestampFormatter dateFormatter, TimestampFormatter timeFormatter,
                          Delimiter delimiter, boolean timeBeforeDate, List<String> dataSetHeaders)
            throws IOException {

        printer = CSVFormat.EXCEL
                .withDelimiter(DELIMITER)
                .print(writer);

        this.dateFormatter = dateFormatter;
        this.timeFormatter = timeFormatter;
        this.delimiter = delimiter;
        this.timeBeforeDate = timeBeforeDate;
        this.dataSetCount = dataSetHeaders.size();

        // write the headers to the first line
        printHeaders(dataSetHeaders);
    }

    private void printHeaders(List<String> dataSetHeaders) throws IOException {

        // print headers for the date and time
        if (timeBeforeDate) {
            printer.print("Time");
            printer.print("Date");
        } else {
            printer.print("Date");
            printer.print("Time");
        }

        // print header for the counts
        printer.print("Count");

        // print statistic header for each data header
        for (String header : dataSetHeaders) {
            for (String statsHeader : STATS_HEADERS) {
                printer.print(header + " - " + statsHeader);
            }
        }

        printer.println();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Public Interface
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Writes a record into the output file. The mean and std deviation values should be provided in the
     * same order as the headers that were specified when building the data file writer. If the number of
     * values in the means/std deviations lists do not correspond to the number of headers, an
     * IllegalArgumentException is thrown.
     *
     * @param timestamp the timestamp for the record.
     * @param count     the number of records in the data.
     * @param means     the means for each data set.
     * @param standardDeviations the standard deviations for each data set.
     * @throws IOException if an IO error occurs.
     * @throws IllegalArgumentException if the number of values in the means//std deviations list is
     * different from the number headers.
     */
    public void write(Timestamp timestamp, long count, List<Double> means, List<Double> standardDeviations)
            throws IOException {

        if (dataSetCount != means.size() || dataSetCount != standardDeviations.size()) {
            throw new IllegalArgumentException("The lists with the mean values and the standard deviations " +
                    "are expected to have " + dataSetCount + " values, but have " + means.size() + " and "
                    + standardDeviations.size() + " values, respectively.");
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
        private List<String> headers = Collections.emptyList();

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

        public Builder withHeaders(List<String> headers) {
            this.headers = headers;
            return this;
        }

        public DataFileWriter build() throws IOException {

            return new DataFileWriter(writer,
                    TimestampFormatter.ofPattern(datePattern),
                    TimestampFormatter.ofPattern(timePattern),
                    sameColumn ? delimiter : null,
                    timeBeforeDate, headers);
        }

    }

}
