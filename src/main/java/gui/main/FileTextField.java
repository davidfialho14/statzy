package gui.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FileTextField extends HBox implements Initializable {

    @FXML private TextField textField;
    @FXML private ImageView checkMarkImageView;

    // Load images before-hand
    private Image validCheckMark = new Image("/images/valid_check_mark.png");
    private Image invalidCheckMark = new Image("/images/invalid_check_mark.png");

    private File file = new File("");

    /**
     * Creates an HBox layout with spacing = 0.
     */
    public FileTextField() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmls/file_text_field.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkMarkImageView.setImage(invalidCheckMark);

        textField.textProperty().addListener((observable, oldText, newText) -> {
            //assign file to the new path
            file = new File(newText);

            // adjust the check mark to valid if fil exists or invalid if otherwise
            checkMarkImageView.setImage(file.isFile() ? validCheckMark : invalidCheckMark);
        });
    }

}
