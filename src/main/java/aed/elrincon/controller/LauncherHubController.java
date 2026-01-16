package aed.elrincon.controller;

import java.io.IOException;

import aed.elrincon.App;
import javafx.fxml.FXML;


public class LauncherHubController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("csv-view");
    }
}
