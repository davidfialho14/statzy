package gui.main;

import java.io.File;

/**
 *
 */
public class OutputFileTextField extends FileTextField {

    @Override
    protected boolean isValid(File file) {
        File parentDirectory = file.getParentFile();
        return parentDirectory != null && parentDirectory.exists();
    }

}
