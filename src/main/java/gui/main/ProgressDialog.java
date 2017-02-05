package gui.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by david on 05-02-2017.
 */
public class ProgressDialog extends Stage {

    public ProgressDialog() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmls/progress_dialog/view.fxml"));
        ProgressDialogController controller = new ProgressDialogController();
        fxmlLoader.setController(controller);
        fxmlLoader.setRoot(controller);

        try {
            setScene(new Scene(fxmlLoader.load()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
