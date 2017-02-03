package core;

import java.text.ParseException;

/**
 * Thrown when the size of a parsed record is not the expected size.
 */
public class IllegalRecordSizeException extends ParseException {

    private final int expectedSize;
    private final int actualSize;

    public IllegalRecordSizeException(String message, int errorOffset, int expectedSize, int actualSize) {
        super(message, errorOffset);
        this.expectedSize = expectedSize;
        this.actualSize = actualSize;
    }

    public int getExpectedSize() {
        return expectedSize;
    }

    public int getActualSize() {
        return actualSize;
    }

}
