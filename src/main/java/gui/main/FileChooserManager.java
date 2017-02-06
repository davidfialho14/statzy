package gui.main;

import javafx.stage.FileChooser;

/**
 * Static class to manage a unique file chooser.
 */
public enum  FileChooserManager {
    INSTANCE;

    private static final FileChooser fileChooser = new FileChooser();

    public static FileChooser getFileChooser() {
        return fileChooser;
    }

}
