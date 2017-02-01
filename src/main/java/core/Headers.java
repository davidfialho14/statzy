package core;

import java.util.List;

/**
 * Structure to store all specific headers read from an headers file. Includes only data headers for data
 * columns that are not ignored. Includes the date and time headers. If the date and time are in the same
 * column then the date and time headers are exactly the same.
 */
public class Headers {

    private final List<String> dataHeaders;
    private final String dateHeader;
    private final String timeHeader;

    /**
     * Constructor should only be called by the HeadersReader class.
     * Initializes the headers structure. The data headers list must be given in the same order as the
     * headers were placed in the headers file and must not include ignored columns.
     *
     * @param dataHeaders   the non-ignored data headers, not null.
     * @param dateHeader    the date header, not null.
     * @param timeHeader    the time header, not null.
     */
    Headers(List<String> dataHeaders, String dateHeader, String timeHeader) {
        this.dataHeaders = dataHeaders;
        this.dateHeader = dateHeader;
        this.timeHeader = timeHeader;
    }

    /**
     * Returns a list with the headers corresponding to the data columns. The list does not include ignored
     * columns and the headers are in the same order as they were read from the headers file.
     *
     * @return the list with the headers corresponding to the data columns, not null.
     */
    public List<String> getDataHeaders() {
        return dataHeaders;
    }

    /**
     * Returns the date header.
     *
     * @return the date header, not null.
     */
    public String getDateHeader() {
        return dateHeader;
    }

    /**
     * Returns the time header.
     *
     * @return the time header, not null.
     */
    public String getTimeHeader() {
        return timeHeader;
    }

}
