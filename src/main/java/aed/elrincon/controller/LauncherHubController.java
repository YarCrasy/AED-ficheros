package aed.elrincon.controller;

import java.io.IOException;

import aed.elrincon.App;
import javafx.fxml.FXML;


public class LauncherHubController {

    @FXML
    public void toCSV() throws IOException {
        App.setRoot("csv-view");
    }

    @FXML
    public void toJSON() throws IOException {
        App.setRoot("json-view");
    }

    @FXML
    public void toXML() throws IOException {
        App.setRoot("xml-view");
    }

    @FXML
    public void toOBJ() throws IOException {
        App.setRoot("obj-view");
    }

}
