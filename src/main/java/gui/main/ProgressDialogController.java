package gui.main;

import core.ProgressListener;
import core.StatisticsGenerator;
import core.Timestamp;
import core.TimestampFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ProgressDialogController extends GridPane implements ProgressListener {

    @FXML private Label statusLabel;
    @FXML private Label betweenLabel;
    @FXML private Label andLabel;
    @FXML private Label lowerBoundLabel;
    @FXML private Label upperBoundLabel;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private static final TimestampFormatter formatter = TimestampFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    /**
     * Notifies the progress listener that a new period is beginning to be processed. Tells the progress
     * listener what are the lower-bound and upper-bound timestamps of the period being processed. Not the
     * upper-bound value is not included in the period, for instance if the period is of 5 seconds and the
     * upper-bound is 10:12:05 then the period comprises only data relative to time before that time.
     *
     * @param lowerBound the lower-bound value of the period currently being processed.
     * @param upperBound the upper-bound value of the period currently being processed.
     */
    @Override
    public void notifyProcessingPeriod(Timestamp lowerBound, Timestamp upperBound) {
        lowerBoundLabel.setText(formatter.format(lowerBound));
        upperBoundLabel.setText(formatter.format(upperBound));
    }

    /**
     * Notifies the listener that the processing has finished.
     */
    @Override
    public void notifyFinished() {
        statusLabel.setText("Done");
        betweenLabel.setText("");
        andLabel.setText("");
        lowerBoundLabel.setText("");
        upperBoundLabel.setText("");

        saveButton.setDisable(false);
    }

    public void save() {
        Stage stage = (Stage) getScene().getWindow();
        stage.close();
    }

    public void cancel() {
        Stage stage = (Stage) getScene().getWindow();
        stage.close();
    }

}
