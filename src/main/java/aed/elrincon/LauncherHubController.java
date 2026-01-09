package aed.elrincon;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LauncherHubController {

    @FXML
    private void switchToSecondary() throws IOException {
        loadFXML("secondary");
    }

    @FXML
    private void switchToCsvView() throws IOException {
        loadFXML("csv-view");
    }

    private void loadFXML(String fxmlName) throws IOException {
        // Obtener el stage actual
        Stage currentStage = (Stage) javafx.scene.Node.class.cast(javafx.scene.control.Button.class).getScene().getWindow();

        // Cargar el nuevo FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName + ".fxml"));
        Parent root = loader.load();

        // Crear nueva escena
        Scene scene = new Scene(root, 1280, 720);

        // Cambiar la escena
        currentStage.setScene(scene);
    }
}