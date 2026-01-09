package aed.elrincon;


import aed.elrincon.App;
import java.io.IOException;
import javafx.fxml.FXML;


import java.io.IOException;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    private void switchToCsvView() throws IOException {
        App.setRoot("csv-view");
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}