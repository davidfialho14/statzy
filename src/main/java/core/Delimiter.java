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

    public static Delimiter DEFAULT = SPACE;

    private final String name;
    private final String character;

    Delimiter(String name, String character) {
        this.name = name;
        this.character = character;
    }

    public String getName() {
        return name;
    }

    public String getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return character;
    }
}
