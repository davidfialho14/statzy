package gui.main;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ProgressDialogController extends GridPane {

    @FXML private Label statusLabel;
    @FXML private Label messageLabel;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private ProgressBar progressBar;

    /**
     * Adjusts the progress dialog to indicate to the user the processing is complete.
     */
    public void onFinished() {
        statusLabel.setText("Done");
        messageLabel.textProperty().unbind();
        messageLabel.setText("");
        saveButton.setDisable(false);
        progressBar.setProgress(1);
    }

    /**
     * Adjusts the progress dialog in case of processing fails. By default just closes the dialog.
     */
    public void onFailed() {
        close();
    }

    /**
     * Returns the property corresponding to the progress message being shown to the user.
     *
     * @return the property corresponding to the progress message being shown to the user.
     */
    public StringProperty messageProperty() {
        return messageLabel.textProperty();
    }

    public void save() {
        close();
    }

    /**
     * Closes the progress dialog on failure.
     */
    private void close() {
        Stage stage = (Stage) getScene().getWindow();
        stage.close();
    }

}
