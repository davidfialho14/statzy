package core;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The headers reader is used to read header files. It takes a CSV file an expects the file to have one or
 * two records. The first record contains the header names and the second record (optional) contains the
 * header units. An header is composed by its name and an optional unit tag that is included between
 * parenthesis. For instance, if the header name is 'Header' and the unit tag is 'm' then the header is
 * 'Header (m)'.
 */
public class HeadersReader implements Closeable {

    private final HeadersParser parser;
    private final int dateColumn;
    private final int timeColumn;
    private final Set<Integer> ignoredColumns;

    HeadersReader(HeadersParser parser, int dateColumn, int timeColumn, Set<Integer> ignoredColumns)
            throws IOException {

        this.parser = parser;
        this.dateColumn = dateColumn;
        this.timeColumn = timeColumn;
        this.ignoredColumns = ignoredColumns;
    }

    /**
     * Creates a headers reader with the date, time, ignored columns specified.
     *
     * @param reader         the underlying reader used to read the headers file.
     * @param dateColumn     the number of the date column (columns start at 0).
     * @param timeColumn     the number of the time column (columns start at 0).
     * @param ignoredColumns the numbers of the columns to be ignored (columns start at 0).
     * @throws IOException if an IO error occurs.
     */
    public HeadersReader(Reader reader, int dateColumn, int timeColumn, Set<Integer> ignoredColumns)
            throws IOException {

        this(new HeadersParser(reader), dateColumn, timeColumn, ignoredColumns);
    }

    /**
     * Creates a headers reader with the date, time, ignored columns specified. Calls the previous
     * constructor with a FileReader.
     *
     * @param headersFile    the path to the headers file.
     * @param dateColumn     the number of the date column (columns start at 0).
     * @param timeColumn     the number of the time column (columns start at 0).
     * @param ignoredColumns the numbers of the columns to be ignored (columns start at 0).
     * @throws IOException if an IO error occurs.
     */
    public HeadersReader(File headersFile, int dateColumn, int timeColumn, Set<Integer> ignoredColumns)
            throws IOException {

        this(new FileReader(headersFile), dateColumn, timeColumn, ignoredColumns);
    }

    /**
     * Reads the headers from the headers file. The file is read completely after calling this method.
     * There is no need to call read() multiple times.
     *
     * @return headers objects containing the headers parsed from the file.
     * @throws IOException if an IO error occurs.
     * @throws ParseException if the format of the header files is not as expected.
     */
    public Headers read() throws IOException, ParseException {

        List<String> headers = parser.parse();

        // check if there are enough headers to include the date and time columns
        int minimumHeadersRequired = Math.max(dateColumn, timeColumn) + 1;
        if (headers.size() < minimumHeadersRequired) {
            throw new ParseException("Headers file was expected to have at least " + minimumHeadersRequired +
                    " header names, but got only " + headers.size() + ".", 1);
        }

        // get the date and time headers
        String dateHeader = headers.get(dateColumn);
        String timeHeader = headers.get(timeColumn);

        // get only the headers for data columns that are not ignored
        List<String> dataHeaders = IntStream.range(0, headers.size())
                .filter(i -> i != dateColumn && i != timeColumn && !ignoredColumns.contains(i))
                .mapToObj(headers::get)
                .collect(Collectors.toList());

        return new Headers(dataHeaders, dateHeader, timeHeader);
    }

    /**
     * Closes the underlying CSV parser.
     *
     * @throws IOException if an IO error occurs.
     */
    @Override
    public void close() throws IOException {
        parser.close();
    }

    /**
     * Returns a list with the header names contain in the specified record.
     *
     * @param namesRecord the record with the names to parse, not null.
     * @return a list with the header names contain in the specified record.
     * @throws ParseException if the record contains less then the minimum of headers required @see
     * {@link #getMinimumHeadersRequired()}.
     */
    private List<String> parseHeaderNames(CSVRecord namesRecord) throws ParseException {

        // check if there are enough headers to include the date and time columns
        int minimumHeadersRequired = getMinimumHeadersRequired();
        if (namesRecord.size() < minimumHeadersRequired) {
            throw new ParseException("Headers file was expected to have at least " + minimumHeadersRequired +
                    " header names, but got only " + namesRecord.size(), 1);
        }

        List<String> names = new ArrayList<>(namesRecord.size());
        namesRecord.forEach(name -> names.add(name.trim()));

        return names;
    }

    /**
     * Parses the unit tags record looking for uni tags. Once a unit tag is found, it is appended to the
     * header in the respective column. Empty unit tags are ignored.
     *
     * @param headers        the list with the headers to append unit tags to, not null.
     * @param unitTagsRecord the record to parse for unit tags, not null.
     * @throws ParseException if the number of unit tag columns is different from the number of headers.
     */
    private void appendHeaderUnitTagsTo(List<String> headers, CSVRecord unitTagsRecord)
            throws ParseException {

        if (unitTagsRecord.size() != headers.size()) {
            throw new ParseException("Headers file has " + headers.size() + " headers and " +
                    unitTagsRecord.size() + " unit tag columns", 2);
        }

        // add the unit tags to the headers
        for (int i = 0; i < headers.size(); i++) {
            String unitTag = unitTagsRecord.get(i).trim();

            if (!unitTag.isEmpty()) {
                headers.set(i, headers.get(i) + " (" + unitTag + ")");
            }

        }

    }

    /**
     * Returns the maximum between the date and the time columns. There must be at east enough headers to
     * include the date and time headers.
     *
     * @return the maximum between the date and the time columns
     */
    private int getMinimumHeadersRequired() {
        return Math.max(dateColumn, timeColumn) + 1;
    }

}
