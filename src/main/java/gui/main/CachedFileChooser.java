package gui.main;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Implementation of a file chooser the caches the directory of the last selected file and opens-up the
 * file chooser in the that same directory in the next dialog.
 */
public class CachedFileChooser {

    private final FileChooser fileChooser = new FileChooser();

    /**
     * Shows a new file open dialog. The method doesn't return until the
     * displayed open dialog is dismissed. The return value specifies
     * the file chosen by the user or {@code null} if no selection has been
     * made. If the owner window for the file dialog is set, input to all
     * windows in the dialog's owner chain is blocked while the file dialog
     * is being shown.
     *
     * @param ownerWindow the owner window of the displayed file dialog
     * @return the selected file or {@code null} if no file has been selected
     */
    public File showOpenDialog(Window ownerWindow) {
        File chosenFile = fileChooser.showOpenDialog(ownerWindow);

        if (chosenFile != null && chosenFile.getParentFile() != null) {
            fileChooser.setInitialDirectory(chosenFile.getParentFile());
        }

        return chosenFile;
    }

    /**
     * Shows a new file save dialog. The method doesn't return until the
     * displayed file save dialog is dismissed. The return value specifies the
     * file chosen by the user or {@code null} if no selection has been made.
     * If the owner window for the file dialog is set, input to all windows in
     * the dialog's owner chain is blocked while the file dialog is being shown.
     *
     * @param ownerWindow the owner window of the displayed file dialog
     * @return the selected file or {@code null} if no file has been selected
     */
    public File showSaveDialog(Window ownerWindow) {
        return fileChooser.showSaveDialog(ownerWindow);
    }
}
