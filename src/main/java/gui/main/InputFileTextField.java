package gui.main;

import java.io.File;

/**
 *
 */
public class InputFileTextField extends FileTextField {

    @Override
    protected boolean isValid(File file) {
        return file.isFile();
    }

}
