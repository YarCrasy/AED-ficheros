package aed.elrincon;

import java.io.IOException;

import javafx.fxml.FXML;


public class LauncherHubController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("csv-view");
    }
}
