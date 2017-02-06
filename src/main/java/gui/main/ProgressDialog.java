package gui.main;

import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Created by david on 05-02-2017.
 */
public class ProgressDialog extends Stage {

    private final ProgressDialogController controller;

    public ProgressDialog(Window owner) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmls/progress_dialog/view.fxml"));
        controller = new ProgressDialogController();
        fxmlLoader.setController(controller);
        fxmlLoader.setRoot(controller);

        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);

        try {
            setScene(new Scene(fxmlLoader.load()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the property corresponding to the progress message being shown to the user.
     *
     * @return the property corresponding to the progress message being shown to the user.
     */
    public StringProperty messageProperty() {
        return controller.messageProperty();
    }

    /**
     * Adjusts the progress dialog to indicate to the user the processing is complete.
     */
    public void onFinished() {
        controller.onFinished();
    }

    /**
     * Adjusts the progress dialog in case of processing fails. By default just closes the dialog.
     */
    public void onFailed() {
        controller.onFailed();
    }


    public void setMessage(String message) {
        controller.messageProperty().setValue(message);
    }

    public void setOnSaveClicked(EventHandler<Event> eventHandler) {
        controller.setOnSaveClicked(eventHandler);
    }
}
