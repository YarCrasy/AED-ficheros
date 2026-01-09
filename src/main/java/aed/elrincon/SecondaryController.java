package aed.elrincon;

import java.io.IOException;

import javafx.fxml.FXML;

// Controlador del módulo de ejemplo
public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("laucher");
    }
}