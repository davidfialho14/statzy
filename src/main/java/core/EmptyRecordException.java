package core;

import java.text.ParseException;

/**
 * Thrown when an empty record is read.
 */
public class EmptyRecordException extends ParseException {

    public EmptyRecordException(String message, int lineNumber) {
        super(message, lineNumber);
    }
}
