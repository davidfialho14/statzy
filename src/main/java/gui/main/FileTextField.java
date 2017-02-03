package gui.main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

public abstract class FileTextField extends HBox implements Initializable {

    // Load images before-hand
    private static Image validImage;
    private static Image invalidImage;

    public static void setImages(Image validImage, Image invalidImage) {
        FileTextField.validImage = validImage;
        FileTextField.invalidImage = invalidImage;
    }

    @FXML private TextField textField;
    @FXML private ImageView checkMarkImageView;

    private File file = new File("");
    private BooleanProperty isValid = new SimpleBooleanProperty(false);

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

    public void initialize(URL location, ResourceBundle resources) {

        textField.textProperty().addListener((observable, oldText, newText) -> {
            //assign file to the new path
            file = new File(newText);
            isValid.setValue(isValid(file));

            // adjust the check mark to valid/invalid
            updateImage();
        });

        isValid.addListener((observable, oldValue, newValue) -> { if (oldValue != newValue) updateImage(); });
    }

    public BooleanProperty validityProperty() {
        return isValid;
    }

    public void updateImage() {
        checkMarkImageView.setImage(isValid() ? validImage : invalidImage);
    }

    public void setFile(File file) {
        this.file = file;
        textField.setText(file.getPath());
        isValid.setValue(isValid(file));
    }

    public boolean isValid() {
        return isValid.getValue();
    }

    abstract protected boolean isValid(File file);

}
