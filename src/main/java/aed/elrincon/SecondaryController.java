package aed.elrincon;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        loadFXML("laucher");
    }

    private void loadFXML(String fxmlName) throws IOException {
        Stage currentStage = (Stage) javafx.scene.Node.class.cast(javafx.scene.control.Button.class).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName + ".fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1280, 720);
        currentStage.setScene(scene);
    }
}