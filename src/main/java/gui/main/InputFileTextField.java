package gui.main;

/**
 *
 */
public class InputFileTextField extends FileTextField {

    @Override
    protected boolean isValid() {
        return file.isFile();
    }

}
