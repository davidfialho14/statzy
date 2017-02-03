package gui.main;

import core.DataFileWriter;
import core.DataRecordFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
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
    @FXML private ChoiceBox dateFormatChoiceBox;
    @FXML private ChoiceBox timeFormatChoiceBox;
    @FXML private ChoiceBox delimiterChoiceBox;
    @FXML private Spinner periodSpinner;
    @FXML private ChoiceBox periodUnitChoiceBox;
    @FXML private ToggleButton dateToggle;
    @FXML private ToggleButton timeToggle;
    @FXML private ToggleButton ignoreToggle;
    @FXML private Button runButton;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Private Fields
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private final FileChooser fileChooser = new FileChooser();
    private DataRecordFactory.Builder dataRecordBuilder = null;
    private DataFileWriter.Builder dataFileWriterBuilder = null;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Constructors and Initializer
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

        // set the images used to indicate if the files are valid or not
        FileTextField.setImages(new Image("/images/valid_check_mark.png"),
                new Image("/images/invalid_check_mark.png"));

        // image must be updated for each file!
        dataFileTextField.updateImage();
        headersFileTextField.updateImage();
        outputFileTextField.updateImage();

        // Ensure the data file controls are enabled when the data and headers file are both valid, and
        // disabled if otherwise. Also, ensure the run button is only enabled if the data, headers, and
        // output files are valid.

        dataFileTextField.validityProperty().addListener((observable, wasValid, isValid) -> {
            setDisableDataFileControls(!(isValid && headersFileTextField.isValid()));
            runButton.setDisable(!(isValid && outputFileTextField.isValid() && headersFileTextField.isValid()));
        });

        headersFileTextField.validityProperty().addListener((observable, wasValid, isValid) -> {
            setDisableDataFileControls(!(isValid && dataFileTextField.isValid()));
            runButton.setDisable(!(isValid && dataFileTextField.isValid() && outputFileTextField.isValid()));
        });

        outputFileTextField.validityProperty().addListener((observable, wasValid, isValid) -> {
            runButton.setDisable(!(isValid && dataFileTextField.isValid() && headersFileTextField.isValid()));
        });
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

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Private Helper Methods
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void setDisableDataFileControls(boolean disable) {
        dateFormatChoiceBox.setDisable(disable);
        timeFormatChoiceBox.setDisable(disable);
        delimiterChoiceBox.setDisable(disable);
        periodSpinner.setDisable(disable);
        periodUnitChoiceBox.setDisable(disable);
        dateToggle.setDisable(disable);
        timeToggle.setDisable(disable);
        ignoreToggle.setDisable(disable);
    }

}
