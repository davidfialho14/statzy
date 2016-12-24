package core;

/**
 * Created by david on 24-12-2016.
 *
 * Enumerator with for all possible split characters that can be used to split dates and times.
 */
public enum SplitCharacter {

    NONE("None", ""),
    COMMA("Comma", ","),
    SPACE("Space", " "),
    SEMI_COLON("Semicolon", ";"),
    TAB("Tab", "\t");

    private final String name;
    private final String character;

    SplitCharacter(String name, String character) {
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
