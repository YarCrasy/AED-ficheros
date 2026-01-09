package aed.elrincon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el FXML del menú principal
        Parent root = FXMLLoader.load(getClass().getResource("laucher.fxml"));

        // Configurar la escena
        Scene scene = new Scene(root, 1280, 720);

        // Configurar el stage
        primaryStage.setTitle("Gestión de Estudiantes - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Iniciar la aplicación JavaFX
        launch(args);
    }
}