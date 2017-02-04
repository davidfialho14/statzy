package gui.main;

import core.*;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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
    @FXML private ChoiceBox<String> dateFormatChoiceBox;
    @FXML private ChoiceBox<String> timeFormatChoiceBox;
    @FXML private ChoiceBox<DelimiterOption> delimiterChoiceBox;
    @FXML private Spinner<Integer> periodSpinner;
    @FXML private ChoiceBox<Unit> periodUnitChoiceBox;
    @FXML private Button runButton;
    @FXML private PreviewTable previewTable;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Private Fields
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private final FileChooser fileChooser = new FileChooser();

    private static final String[] DATE_FORMATS = {
            "dd/MM/uuuu", "dd/MMM/uu", "dd/MMM/uuuu", "dd/MM/uu",
            "dd-MM-uuuu", "dd-MMM-uu", "dd-MMM-uuuu", "dd-MM-uu",
            "uuMMdd"
    };
    private static final String[] TIME_FORMATS = {
            "HH:mm:ss", "HHmmss", "HH:mm"
    };

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

        headersFileTextField.validityProperty().addListener((observable, wasValid, isValid) -> {
            if (wasValid && !isValid) { // became invalid
                previewTable.clearHeaders();
            }
        });

        dataFileTextField.validityProperty().addListener((observable, wasValid, isValid) -> {
            if (wasValid && !isValid) { // became invalid
                previewTable.clearData();
            }
        });

        // add options to the choice boxes
        dateFormatChoiceBox.getItems().addAll(DATE_FORMATS);
        timeFormatChoiceBox.getItems().addAll(TIME_FORMATS);
        delimiterChoiceBox.getItems().addAll(DelimiterOption.values());
        periodUnitChoiceBox.getItems().addAll(Unit.values());

        // select default values
        dateFormatChoiceBox.getSelectionModel().selectFirst();
        timeFormatChoiceBox.getSelectionModel().selectFirst();
        delimiterChoiceBox.getSelectionModel().selectFirst();
        periodUnitChoiceBox.getSelectionModel().selectFirst();

        // set the period spinner to take only value over 0
        periodSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Public Interface
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void chooseDataFile(ActionEvent actionEvent) {
        File chosenFile = chooseInputFile(dataFileTextField);

        if (chosenFile != null) {
            if (dataFileTextField.isValid()) {
                previewData();
            } else {
                previewTable.clearData();
            }
        }
    }

    public void chooseHeadersFile(ActionEvent actionEvent) {
        File chosenFile = chooseInputFile(headersFileTextField);

        if (chosenFile != null && headersFileTextField.isValid()) {
            previewHeaders();
        }
    }

    private File chooseInputFile(InputFileTextField fileTextField) {
        File file = fileChooser.showOpenDialog(mainPain.getScene().getWindow());

        if (file != null) {
            fileTextField.setFile(file);
        }

        return file;
    }

    public void chooseOutputPath(ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(mainPain.getScene().getWindow());

        if (file != null) {
            outputFileTextField.setFile(file);
        }
    }

    public void run(ActionEvent actionEvent) {

        Headers headers;
        try (HeadersReader headersReader = new HeadersReader(headersFileTextField.getFile(),
                previewTable.getDateColumn(), previewTable.getTimeColumn(),
                previewTable.getIgnoredColumns())) {

            headers = headersReader.read();

        } catch (IOException e) {
            errorAlert(e.getMessage(), "Headers File Error").showAndWait();
            return;
        } catch (ParseException e) {
            errorAlert("Headers file probably has an error in line " + e.getErrorOffset() + ".\n" +
                    e.getMessage(), "Process Error").showAndWait();
            return;
        }

        try (
                DataRecordReader reader = DataRecordReader.with(dataFileTextField.getFile())
                        .withDateInColumn(previewTable.getDateColumn())
                        .withDatePattern(dateFormatChoiceBox.getValue())
                        .withTimeInColumn(previewTable.getTimeColumn())
                        .withTimePattern(timeFormatChoiceBox.getValue())
                        .delimitedBy(delimiterChoiceBox.getSelectionModel().getSelectedItem().getDelimiter())
                        .ignoreColumns(previewTable.getIgnoredColumns())
                        .build();

                DataFileWriter writer = DataFileWriter.outputTo(outputFileTextField.getFile())
                        .withHeaders(headers)
                        .withDatePattern(dateFormatChoiceBox.getValue())
                        .withTimePattern(timeFormatChoiceBox.getValue())
                        .delimitedBy(delimiterChoiceBox.getSelectionModel().getSelectedItem().getDelimiter())
                        .inSameColumn(previewTable.getDateColumn() == previewTable.getTimeColumn())
                        .build()
        ) {

            StatisticsGenerator statisticsGenerator = new StatisticsGenerator();
            statisticsGenerator.process(reader, writer, Period.of(periodSpinner.getValue(),
                    periodUnitChoiceBox.getValue()));

        } catch (IOException e) {
            errorAlert(e.getMessage(), "Process Error").showAndWait();
        } catch (ParseException e) {
            errorAlert("Data file probably has an error in line " + e.getErrorOffset() + ".\n" +
                            e.getMessage(), "Process Error").showAndWait();
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
    }

    private static final int PREVIEW_RECORD_COUNT = 5;

    private void previewData() {

        try (RecordParser parser = new RecordParser(dataFileTextField.getFile())) {

            List<Record> previewRecords = new ArrayList<>(PREVIEW_RECORD_COUNT);

            for (int i = 0; i < PREVIEW_RECORD_COUNT; i++) {
                Record record = parser.parseRecord();
                if (record == null) break;

                previewRecords.add(record);
            }

            previewTable.setData(previewRecords);

        } catch (IOException | IllegalRecordSizeException | PreviewException e) {
            dataFileTextField.setInvalid();
            errorAlert(e.getMessage(), "Data Preview Error").showAndWait();
        }
    }

    private void previewHeaders() {

        try (HeadersParser parser = new HeadersParser(headersFileTextField.getFile())) {
            List<String> headers = parser.parse();
            previewTable.setHeaders(headers);

        } catch (IOException | ParseException | PreviewException e) {
            headersFileTextField.setInvalid();
            errorAlert(e.getMessage(), "Headers Preview Error").showAndWait();
        }

    }

    private static Alert errorAlert(String message, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR, WordUtils.wrap(message, 50), ButtonType.OK);
        alert.setHeaderText(header);
        return alert;
    }

}
