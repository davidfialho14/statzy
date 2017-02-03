package gui.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Private Inner Nodes
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML private GridPane mainPain;
    @FXML private InputFileTextField dataFileTextField;
    @FXML private InputFileTextField headersFileTextField;
    @FXML private OutputFileTextField outputFileTextField;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Private Fields
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private final FileChooser fileChooser = new FileChooser();

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Constructors and Initializer
     *
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
        FileTextField.setImages(new Image("/images/valid_check_mark.png"),
                new Image("/images/invalid_check_mark.png"));

        dataFileTextField.updateImage();
        headersFileTextField.updateImage();
        outputFileTextField.updateImage();

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Public Interface
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void chooseDataFile(ActionEvent actionEvent) {
        chooseInputFile(dataFileTextField);
    }

    public void chooseHeadersFile(ActionEvent actionEvent) {
        chooseInputFile(headersFileTextField);
    }

    private void chooseInputFile(InputFileTextField fileTextField) {
        File file = fileChooser.showOpenDialog(mainPain.getScene().getWindow());

        if (file != null) {
            fileTextField.setFile(file);
        }
    }

    public void chooseOutputPath(ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(mainPain.getScene().getWindow());

        if (file != null) {
            outputFileTextField.setFile(file);
        }
    }

    /**
     * Invoked when the close button is clicked. Closes the program.
     */
    public void close() {
        Stage stage = (Stage) mainPain.getScene().getWindow();
        stage.close();
    }
}
