package aed.elrincon.controller;

import java.io.IOException;

import aed.elrincon.App;
import javafx.fxml.FXML;

// Controlador del módulo de ejemplo
public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("laucher");
    }
}