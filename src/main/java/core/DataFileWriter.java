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

    private final CSVPrinter printer;
    private final TimestampFormatter formatter;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Constructors
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public DataFileWriter(Writer writer, TimestampFormatter formatter) throws IOException {
        printer = CSVFormat.EXCEL
                .withDelimiter(',')
                .print(writer);

        this.formatter = formatter;
    }

    public DataFileWriter(File csvFile, TimestampFormatter formatter) throws IOException {
        this(new FileWriter(csvFile), formatter);
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

        printer.print(formatter.format(timestamp));
        printer.print(count);

        Iterator<Double> meansIterator = means.iterator();
        Iterator<Double> standardDevationsIterator = standardDeviations.iterator();

        while (meansIterator.hasNext()) {   // while
            printer.print(meansIterator.next());
            printer.print(standardDevationsIterator.next());
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


}
