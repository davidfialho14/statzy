package gui.main;

import core.Delimiter;

/**
 * Wrapper around a delimiter to work as an option for the GUI. Changes the toString(9 method to return the
 * delimiter's name instead of the character.
 */
public enum DelimiterOption {

    NONE(Delimiter.NONE),
    COMMA(Delimiter.COMMA),
    SPACE(Delimiter.SPACE),
    SEMI_COLON(Delimiter.SEMI_COLON),
    TAB(Delimiter.TAB);

    private final Delimiter delimiter;

    DelimiterOption(Delimiter delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String toString() {
        return delimiter.getName();
    }

}
