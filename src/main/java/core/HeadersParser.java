package core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by david on 03-02-2017.
 */
public class HeadersParser implements Closeable {

    private final RecordParser recordParser;

    public HeadersParser(RecordParser parser) {
        recordParser = parser;
    }

    public HeadersParser(Reader reader) throws IOException {
        this(new RecordParser(reader));
    }

    public HeadersParser(File headersFile) throws IOException {
        this(new RecordParser(headersFile));
    }

    public List<String> parse() throws ParseException {

        Record namesRecord = recordParser.parseRecord();
        if (namesRecord == null) {
            throw new ParseException("Headers file is empty.", 1);
        }

        List<String> headers = namesRecord.stream()
                .map(String::trim)  // remove extra white spaces
                .collect(Collectors.toList());

        try {
            Record unitTagsRecord = recordParser.parseRecord();

            if (unitTagsRecord != null) {

                // add the unit tags to the headers
                for (int i = 0; i < headers.size(); i++) {
                    String unitTag = unitTagsRecord.get(i).trim();

                    if (!unitTag.isEmpty()) {
                        headers.set(i, headers.get(i) + " (" + unitTag + ")");
                    }
                }
            }

        } catch (IllegalRecordSizeException e) {
            throw new ParseException("The row with the header names and the row with the unit tags do " +
                    "not have the same number of columns: names row has " + headers.size() + " and unit" +
                    " tags row has " + e.getActualSize() + " columns.", 2);
        }

        return headers;
    }

    /**
     * Closes the underlying record parser.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        recordParser.close();
    }

}
