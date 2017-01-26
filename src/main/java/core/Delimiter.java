package core;

/**
 * Collection of delimiters that can be used to separate date and times in the same column.
 */
public enum Delimiter {

    NONE("None", ""),
    COMMA("Comma", ","),
    SPACE("Space", " "),
    SEMI_COLON("Semicolon", ";"),
    TAB("Tab", "\t");

    private final String name;
    private final String character;

    Delimiter(String name, String character) {
        this.name = name;
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return name;
    }
}
